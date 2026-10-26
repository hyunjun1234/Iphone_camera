package studio.moment.camera.camera

import android.graphics.SurfaceTexture
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLSurface
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import androidx.camera.core.CameraEffect
import androidx.camera.core.ProcessingException
import androidx.camera.core.SurfaceOutput
import androidx.camera.core.SurfaceProcessor
import androidx.camera.core.SurfaceRequest
import studio.moment.camera.color.ColorLut
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.Executor

/** CameraX handles sensor rotation, viewport cropping and front-camera mirroring via SurfaceOutput. */
class LutSurfaceProcessor(private val onError: (Throwable) -> Unit) : SurfaceProcessor, AutoCloseable {
    private val thread = HandlerThread("moment-color").apply { start() }
    private val handler = Handler(thread.looper)
    val executor = Executor { handler.post(it) }
    val effect = object : CameraEffect(PREVIEW, executor, this, { onError(it) }) {}
    @Volatile var lut: ColorLut = ColorLut.identity
    @Volatile var compareOriginal = false
    private var uploaded: ColorLut? = null
    private var display: EGLDisplay = EGL14.EGL_NO_DISPLAY
    private var context: EGLContext = EGL14.EGL_NO_CONTEXT
    private var config: EGLConfig? = null
    private var pbuffer: EGLSurface = EGL14.EGL_NO_SURFACE
    private var program = 0
    private var lutTexture = 0
    private var closing = false
    private var failed = false
    private val inputs = mutableMapOf<SurfaceTexture, Int>()
    private val outputs = mutableMapOf<SurfaceOutput, EGLSurface>()
    private val vertices = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer()
        .put(floatArrayOf(-1f, -1f, 1f, -1f, -1f, 1f, 1f, 1f)).apply { position(0) }
    private val originalTransform = FloatArray(16)
    private val outputTransform = FloatArray(16)

    override fun onInputSurface(request: SurfaceRequest) {
        if (closing || failed) { request.willNotProvideSurface(); return }
        try {
            initialize()
            makeCurrent(pbuffer)
            val texture = newTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES)
            val st = SurfaceTexture(texture)
            st.setDefaultBufferSize(request.resolution.width, request.resolution.height)
            val surface = Surface(st)
            inputs[st] = texture
            st.setOnFrameAvailableListener({ draw(it) }, handler)
            request.provideSurface(surface, executor) {
                st.setOnFrameAvailableListener(null)
                st.release()
                surface.release()
                inputs.remove(st)
                if (display != EGL14.EGL_NO_DISPLAY) {
                    makeCurrent(pbuffer)
                    GLES20.glDeleteTextures(1, intArrayOf(texture), 0)
                }
                finishClose()
            }
        } catch (e: Exception) {
            failed = true
            request.willNotProvideSurface()
            onError(e)
            throw ProcessingException().apply { initCause(e) }
        }
    }

    override fun onOutputSurface(output: SurfaceOutput) {
        if (closing || failed) { output.close(); return }
        try {
            initialize()
            val surface = output.getSurface(executor) { removeOutput(output) }
            val window = EGL14.eglCreateWindowSurface(display, config, surface, intArrayOf(EGL14.EGL_NONE), 0)
            check(window != EGL14.EGL_NO_SURFACE) { "미리보기 화면을 만들 수 없습니다." }
            outputs[output] = window
        } catch (e: Exception) {
            failed = true
            output.close()
            onError(e)
            throw ProcessingException().apply { initCause(e) }
        }
    }

    private fun draw(st: SurfaceTexture) {
        if (closing || failed || !inputs.containsKey(st)) return
        try {
            makeCurrent(pbuffer)
            st.updateTexImage()
            st.getTransformMatrix(originalTransform)
            val currentLut = if (compareOriginal) ColorLut.identity else lut
            if (uploaded !== currentLut) {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, lutTexture)
                val bytes = ByteBuffer.allocateDirect(currentLut.colors.size * 4).put(currentLut.rgbaBytes()).apply { position(0) }
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, currentLut.size,
                    currentLut.size * currentLut.size, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, bytes)
                uploaded = currentLut
            }
            for ((output, window) in outputs.toMap()) {
                makeCurrent(window)
                output.updateTransformMatrix(outputTransform, originalTransform)
                GLES20.glViewport(0, 0, output.size.width, output.size.height)
                GLES20.glUseProgram(program)
                val position = GLES20.glGetAttribLocation(program, "aPosition")
                vertices.position(0)
                GLES20.glEnableVertexAttribArray(position)
                GLES20.glVertexAttribPointer(position, 2, GLES20.GL_FLOAT, false, 0, vertices)
                GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(program, "uTransform"), 1, false, outputTransform, 0)
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
                GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, inputs.getValue(st))
                GLES20.glUniform1i(GLES20.glGetUniformLocation(program, "uCamera"), 0)
                GLES20.glActiveTexture(GLES20.GL_TEXTURE1)
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, lutTexture)
                GLES20.glUniform1i(GLES20.glGetUniformLocation(program, "uLut"), 1)
                GLES20.glUniform1f(GLES20.glGetUniformLocation(program, "uSize"), currentLut.size.toFloat())
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
                if (!EGL14.eglSwapBuffers(display, window)) removeOutput(output)
            }
        } catch (e: Exception) {
            if (!closing) { failed = true; onError(e) }
        }
    }

    private fun initialize() {
        if (display != EGL14.EGL_NO_DISPLAY) return
        display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        check(EGL14.eglInitialize(display, IntArray(2), 0, IntArray(2), 0))
        val configs = arrayOfNulls<EGLConfig>(1)
        val count = IntArray(1)
        check(EGL14.eglChooseConfig(display, intArrayOf(EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
            EGL14.EGL_SURFACE_TYPE, EGL14.EGL_WINDOW_BIT or EGL14.EGL_PBUFFER_BIT,
            EGL14.EGL_RED_SIZE, 8, EGL14.EGL_GREEN_SIZE, 8, EGL14.EGL_BLUE_SIZE, 8,
            EGL14.EGL_ALPHA_SIZE, 8, EGL14.EGL_NONE), 0, configs, 0, 1, count, 0) && count[0] > 0)
        config = configs[0]
        context = EGL14.eglCreateContext(display, config, EGL14.EGL_NO_CONTEXT,
            intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE), 0)
        check(context != EGL14.EGL_NO_CONTEXT)
        pbuffer = EGL14.eglCreatePbufferSurface(display, config,
            intArrayOf(EGL14.EGL_WIDTH, 1, EGL14.EGL_HEIGHT, 1, EGL14.EGL_NONE), 0)
        check(pbuffer != EGL14.EGL_NO_SURFACE)
        makeCurrent(pbuffer)
        val vertex = compile(GLES20.GL_VERTEX_SHADER, VERTEX)
        val fragment = compile(GLES20.GL_FRAGMENT_SHADER, FRAGMENT)
        program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertex); GLES20.glAttachShader(program, fragment)
        GLES20.glLinkProgram(program)
        val linked = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linked, 0)
        check(linked[0] != 0) { GLES20.glGetProgramInfoLog(program) }
        GLES20.glDeleteShader(vertex); GLES20.glDeleteShader(fragment)
        lutTexture = newTexture(GLES20.GL_TEXTURE_2D)
    }

    private fun makeCurrent(surface: EGLSurface) {
        check(EGL14.eglMakeCurrent(display, surface, surface, context)) { "GPU 화면 연결에 실패했습니다." }
    }

    private fun newTexture(target: Int): Int {
        val id = IntArray(1)
        GLES20.glGenTextures(1, id, 0)
        GLES20.glBindTexture(target, id[0])
        GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(target, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        return id[0]
    }

    private fun compile(type: Int, source: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, source); GLES20.glCompileShader(shader)
        val ok = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, ok, 0)
        check(ok[0] != 0) { GLES20.glGetShaderInfoLog(shader) }
        return shader
    }

    private fun removeOutput(output: SurfaceOutput) {
        outputs.remove(output)?.let {
            makeCurrent(pbuffer)
            EGL14.eglDestroySurface(display, it)
        }
        output.close()
    }

    override fun close() {
        handler.post {
            closing = true
            outputs.keys.toList().forEach(::removeOutput)
            finishClose()
        }
    }

    private fun finishClose() {
        if (!closing || inputs.isNotEmpty()) return
        if (display != EGL14.EGL_NO_DISPLAY) {
            makeCurrent(pbuffer)
            GLES20.glDeleteProgram(program)
            GLES20.glDeleteTextures(1, intArrayOf(lutTexture), 0)
            EGL14.eglMakeCurrent(display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
            EGL14.eglDestroySurface(display, pbuffer)
            EGL14.eglDestroyContext(display, context)
            EGL14.eglTerminate(display)
            EGL14.eglReleaseThread()
            display = EGL14.EGL_NO_DISPLAY
        }
        thread.quitSafely()
    }

    companion object {
        private const val VERTEX = """
            attribute vec2 aPosition;
            uniform mat4 uTransform;
            varying vec2 vTex;
            void main() {
                gl_Position = vec4(aPosition, 0.0, 1.0);
                vTex = (uTransform * vec4(aPosition * 0.5 + 0.5, 0.0, 1.0)).xy;
            }
        """
        private const val FRAGMENT = """
            #extension GL_OES_EGL_image_external : require
            precision highp float;
            uniform samplerExternalOES uCamera;
            uniform sampler2D uLut;
            uniform float uSize;
            varying vec2 vTex;
            void main() {
                vec3 rgb = clamp(texture2D(uCamera, vTex).rgb, 0.0, 1.0);
                float blue = rgb.b * (uSize - 1.0);
                float slice = floor(blue);
                float x = (rgb.r * (uSize - 1.0) + 0.5) / uSize;
                float y0 = (slice * uSize + rgb.g * (uSize - 1.0) + 0.5) / (uSize * uSize);
                float y1 = (min(slice + 1.0, uSize - 1.0) * uSize + rgb.g * (uSize - 1.0) + 0.5) / (uSize * uSize);
                vec3 low = texture2D(uLut, vec2(x, y0)).rgb;
                vec3 high = texture2D(uLut, vec2(x, y1)).rgb;
                gl_FragColor = vec4(mix(low, high, fract(blue)), 1.0);
            }
        """
    }
}
