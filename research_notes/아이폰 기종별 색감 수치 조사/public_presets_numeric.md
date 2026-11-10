# Public presets, LUTs, profiles and code that imitate iPhone photo looks: numeric values

Research pass: 2026-11-10. Items already in `docs/PUBLIC_COLOR_RECIPES.md` are not repeated. The pass stopped early on a context limit, so several leads are unopened. They are listed under Handoff.

All "parsed locally" numbers were read on 2026-11-10 from files downloaded to `/tmp/iphone_color_dl/` (outside the repository). No file was copied into the repository.

## Which presets or recipes exist per model with readable numbers?

### Takeaway
No new recipe that converts another camera into a specific iPhone model was found with readable numbers. The new per-model numbers are Apple's own DNG metadata (6s Plus, 7 Plus, 8, XS, 12 Pro) and one Lightroom preset that adds a film look to iPhone 11 photos.

### Cited Findings

Per-model status after this pass:

| App model | New numeric material | Kind |
| --- | --- | --- |
| 6s | Apple DNG colour matrix and BaselineExposure from a 6s Plus sample | Sensor calibration, not the JPEG look |
| 7 | Same, from a 7 Plus sample | Sensor calibration |
| 8 | Same, from an iPhone 8 sample | Sensor calibration |
| X | None | - |
| XS | Same, from an XS sample | Sensor calibration |
| 11 | `iPhone11 - Film.xmp` Lightroom preset | Film look added to iPhone 11 photos |
| 13 / 15 Pro | Preset values of the old Photographic Styles | Apple's in-camera presets, read from a search summary only |
| 16 | File-format facts for the iPhone 16+ styles (Shalielie) | Reverse engineering, see the fourth question |

- Apple-written DNG tags, parsed locally from one sample per model. ColorMatrix2 is the D65 matrix (CalibrationIlluminant2 = 21) in the DNG XYZ-to-camera convention. AsShotNeutral and BaselineExposure are per-shot values. Source: [raw.pixls.us Apple folder](https://raw.pixls.us/data/Apple/)

| Sample (UniqueCameraModel, writer, capture date) | ColorMatrix2 rows | BaselineExposure | AsShotNeutral |
| --- | --- | --- | --- |
| iPhone 6s Plus (iPhone8,2, Camera+ 9.0.1, 2016-12-19) | [0.7093, -0.2276, -0.0213] [-0.6052, 1.2490, 0.3011] [-0.1127, 0.1317, 0.5887] | -0.6566 | 0.4636, 1, 0.6048 |
| iPhone SE 1st gen (iPhone8,4, VSCO, 2017-03-23) | identical to 6s Plus | -0.2177 | 0.4550, 1, 0.4928 |
| iPhone 7 Plus (iPhone9,4, ProCamera 10.2, 2017-01-12) | [0.7049, -0.2220, -0.0663] [-0.4634, 1.2188, 0.2053] [-0.0878, 0.1306, 0.5431] | -0.3233 | 0.4022, 1, 0.5871 |
| iPhone 8 (iPhone10,1, iOS 11.3.1, 2018-11-07) | [0.8299, -0.2739, -0.0971] [-0.5729, 1.3635, 0.1709] [-0.0863, 0.1364, 0.5364] | -0.5287 | 0.6442, 1, 0.3520 |
| iPhone XS (iPhone11,2, ProCamera 12.4, 2019-09-07) | [0.7368, -0.2173, -0.0873] [-0.4603, 1.2249, 0.1974] [-0.0821, 0.1363, 0.4730] | tag absent | 0.4336, 1, 0.5160 |
| iPhone 12 Pro ProRAW (iPhone13,3, iOS 14.3, 2020-12-29) | [0.9145, -0.3222, -0.1262] [-0.4289, 1.3095, 0.0947] [-0.1063, 0.2350, 0.4307] | -0.3022 | 0.4490, 1, 0.5409 |

- The five Bayer samples (6s Plus, SE, 7 Plus, 8, XS) carry no ProfileToneCurve tag. Only the 12 Pro ProRAW file has one. Source: [raw.pixls.us Apple folder](https://raw.pixls.us/data/Apple/)
- Recipe: `iPhone11 - Film`. Author: fredrikaverpil. Date: file date not recorded, preset metadata says Camera Raw 12.2.1, repository last pushed 2025-11-08. Input: iPhone 11 photos (from the file name). Target: a film look, not an iPhone imitation. Validation: none stated. Licence: none stated in the repository. Settings as published: ProcessVersion 11.0, WhiteBalance As Shot, Exposure2012 -1.00, Contrast2012 +10, Highlights2012 -44, Shadows2012 +50, Blacks2012 +50, Vibrance +20, Saturation -20, HSL saturation Yellow +15 and Blue +43 (all other HSL 0), calibration hue and saturation all 0, ShadowTint 0, Sharpness 0, LuminanceSmoothing 20, ColorNoiseReduction 25, PostCropVignetteAmount -15, LensProfileEnable 1, CameraProfile Adobe Standard. ToneCurvePV2012 points (0,36) (106,116) (255,237). Red, green and blue curves each (0,0) (67,45) (201,219) (255,255). Source: [fredrikaverpil/photography](https://github.com/fredrikaverpil/photography/blob/HEAD/Lightroom/Presets/iPhone11%20-%20Film.xmp)
- Old Photographic Styles presets (iPhone 13 generation): Standard Tone 0 / Warmth 0, Rich Contrast Tone -50, Vibrant Tone +50, Warm Warmth +50, Cool Warmth -50. Read from a search-result summary only, pages not opened. Source: [BGR](https://bgr.com/tech/this-cool-iphone-feature-automatically-edits-photos-as-you-take-them/), [Cult of Mac](https://www.cultofmac.com/how-to/photo-styles)
- The GitHub API reports no licence for Pondowner857/comfy_Pond_Nodes (checked 2026-11-10). The earlier document did not record a licence. Source: [Pondowner857/comfy_Pond_Nodes](https://github.com/Pondowner857/comfy_Pond_Nodes)

### Inferences
- The DNG matrices describe sensor colour calibration before Apple's rendering. They cannot be turned into per-model saturation or contrast values for an sRGB transform.
- BaselineExposure differs between the 6s Plus and SE samples that share one matrix set, so it is a per-shot value and not a per-model constant.
- The `iPhone11 - Film` preset uses Exposure -1.00 with the Adobe Standard profile, which suggests raw DNG input rather than iPhone JPEG or HEIC. This is not stated by the author.

### Gaps
- No readable numbers for the iPhone 7 Peach tutorial (https://www.youtube.com/watch?v=gSpbaOQgxrc at 7:32) or the XS Max Inspired tutorial (https://www.youtube.com/watch?v=-b02h4UPapo). Video frames were not accessed in this pass.
- No recipe found for iPhone X.
- Lemon8, Pinterest, Xiaohongshu and Korean blog posts were searched only through a general search tool that returned no posts with value lists. Direct site searches were not run.

## Are there open .cube LUT files or code whose values can be read directly?

### Takeaway
Four open code sources and one open .cube file were read. The code gives explicit formulas for a generic "iPhone look". The one LUT with iPhone in its name is a strong teal and orange creative grade with no stated input or target.

### Cited Findings

- Code: `iPhone Look (V8.1 Fixed)` ComfyUI node. Author: ivanprimestars. Date: repository created 2026-04-22. Input: any image tensor (ComfyUI, typically AI-generated). Target: "iPhone-like photo postprocessing", no model. Validation: none stated. Licence: none stated. Defaults: hdr_strength 0.73, shadow_lift 0.36, highlight_rolloff 0.45, sharpness 0.61, skin_preserve 0.72, warmth 0.12, vibrance 0.34, micro_ca 0.05, pixel_destruction 0.20, demosaic_artifacts 0.15, jpeg_quality 94, luma_noise 0.8. Source: [iphone_look_node.py](https://github.com/ivanprimestars/comfyui_iphone_look/blob/HEAD/iphone_look_node.py)
- Formulas in that node, on 0..1 display values. Source: [iphone_look_node.py](https://github.com/ivanprimestars/comfyui_iphone_look/blob/HEAD/iphone_look_node.py)
  - Local contrast: `guide = GaussianBlur(Rec.709 luma, sigma = min(H, W) / 25)`, then `img = exp(log(guide) + (log(img) - log(guide)) * (1 + hdr_strength * 0.25))`. Default detail gain 1.18.
  - Shadow lift: `img += img * shadow_lift * (1 - guide)^2 * 0.5`. Default: up to +18% where the blurred luma is 0.
  - Highlight rolloff: `h = clip((img - 0.65) / 0.35, 0, 1)`, `img /= 1 + highlight_rolloff * h * 0.6`. Default: knee at 0.65, division by 1.27 at 1.0.
  - Warmth: `R *= 1 + warmth * 0.05`, `B *= 1 - warmth * 0.03`. Default: R x1.006, B x0.996.
  - Vibrance: `img += (img - luma) * vibrance * 0.4`. Default saturation gain 1.136.
  - Sharpen: unsharp mask sigma 1.0, amount `sharpness * 1.4`, reduced on skin by `skin_preserve * 0.85`.
- Code: BILD-VizionPipeline "iPhone Authenticate" nodes. Author: EllipsesMark. Date: created 2026-03-24, pushed 2026-04-29. Input: AI-generated images. Target: "real iPhone photos", no model. Validation: none stated in the README. Licence: none stated. Chain order: Auto White Balance, Smart HDR, Deep Fusion, Film Grain, Camera Simulator, Lens Effects, Compression. Source: [BILD-VizionPipeline](https://github.com/EllipsesMark/BILD-VizionPipeline)
  - Auto White Balance: gray-world scaling, then the neutral target is multiplied by R 1.03, G 1.01, B 0.96 at `iphone_bias` 1.0. Default `iphone_bias` 0.3, which gives R x1.009, G x1.003, B x0.988. `color_temperature` default 5500 K, with a shift of +/-0.04 of mean luminance on R and B per +/-3500 K. Source: [auto_white_balance.py](https://github.com/EllipsesMark/BILD-VizionPipeline/blob/HEAD/nodes/auto_white_balance.py)
  - Smart HDR defaults: shadow_lift 0.4, highlight_compress 0.3. Shadows: mask `(1 - luma)^2`, pixel raised to `gamma = 1 - shadow_lift * 0.5` (0.8), blended by `shadow_lift * mask`. Highlights: threshold `1 - highlight_compress * 0.5` (0.85), excess divided by `1 + highlight_compress * 3` (1.9), blended by `highlight_compress * luma^2`. Then the image is mixed toward its mean by `(shadow_lift + highlight_compress) * 0.1` (0.07). Source: [smart_hdr.py](https://github.com/EllipsesMark/BILD-VizionPipeline/blob/HEAD/nodes/smart_hdr.py)
  - Deep Fusion defaults: sharpness 0.6, skin_smooth 0.3. Unsharp sigma 1.5 plus a second band at sigma 3.75 weighted `sharpness * 0.3`. Skin blur sigma `2.0 + skin_smooth * 3.0`. Source: [deep_fusion.py](https://github.com/EllipsesMark/BILD-VizionPipeline/blob/HEAD/nodes/deep_fusion.py)
- Code: Camera Style Pad. Author: ziyuw-Plank. Date: 2026-10-19. Input: Xiaomi HyperOS 3 camera preview (`com.android.camera` 6.4.000530.0) with a stock filter selected. Target: the iPhone 18 Pro Photographic Styles 2D pad. Validation: none stated, and the README says saved photos are not wired yet. Licence: MIT. Source: [Style.java](https://github.com/ziyuw-Plank/camera-style-pad/blob/main/app/src/main/java/io/github/ziyuw_plank/stylepad/Style.java)
  - x from -1 (cool) to +1 (warm): `R *= 1 + 0.14x`, `G *= 1 + 0.03x`, `B *= 1 - 0.14x`.
  - y from -1 (darker, more contrast) to +1 (brighter, flatter): `v = v^(2^(-0.6y))`, then `v = 0.5 + (v - 0.5) * (1 - 0.35y)`.
  - sat from 0 to 2: `c = l + (c - l) * sat` with `l = 0.299R + 0.587G + 0.114B`.
  - Applied to display-referred 0..1 values without linearisation.
- LUT: `iPhone-Looks-8.cube`, 16x16x16, no title or comments. Repository: yashbhal/thecreatormarket, MIT, created 2025-09-27. Input and target: not stated. Validation: none. Parsed locally with trilinear sampling. Source: [iPhone-Looks-8.cube](https://github.com/yashbhal/thecreatormarket/blob/HEAD/styles/luts/iPhone-Looks-8.cube)

| Input (R, G, B) | Output (R, G, B) | Note |
| --- | --- | --- |
| grey 0.10 | 0.024, 0.059, 0.054 | darker, green-cyan cast |
| grey 0.25 | 0.131, 0.177, 0.139 | darker, green cast |
| grey 0.50 | 0.531, 0.531, 0.388 | blue -0.14, yellow cast |
| grey 0.75 | 0.904, 0.866, 0.776 | brighter, warm cast |
| grey 0.90 | 0.975, 0.961, 0.898 | warm cast |
| black 0 | 0.005, 0.025, 0.027 | lifted toward teal |
| white 1 | 0.983, 0.978, 0.934 | warm white |
| red 1, 0, 0 | 0.977, 0.201, 0.108 | hue +6.4 deg |
| green 0, 1, 0 | 0.246, 0.932, -0.004 | hue -15.8 deg |
| blue 0, 0, 1 | 0.005, 0.344, 0.630 | hue -32.5 deg, toward cyan |
| skin 0.761, 0.588, 0.510 | 0.987, 0.598, 0.307 | HSV saturation 0.33 to 0.69 |
| skin 0.451, 0.322, 0.267 | 0.589, 0.283, 0.032 | HSV saturation 0.41 to 0.95 |

### Inferences
- `iPhone-Looks-8.cube` is a creative grade and not an imitation of iPhone rendering. Its cool shadows, warm highlights and large skin saturation gain do not match any description of Apple's default output. Do not use it as a reference.
- The Camera Style Pad constants are the only open parametric model of a Tone and Warmth style pad found. They are the author's choice, not measured values.
- None of the code sources contains per-model parameters. All are single generic recipes.

### Gaps
- `gh search code "iphone" --extension cube` returned nothing, and `gh search code --filename iphone --extension cube` returned only the file above (2026-11-10). No open LUT that claims another camera to iPhone conversion was found.
- Hugging Face spaces and datasets search for "iphone" (first 40 results each) showed no colour transform tool. The models search was not run.
- Commit hashes of the files above were not recorded. The links point at HEAD.
- Not opened: `nodes/authenticate_all.py`, `film_grain.py`, `lens_effects.py`, `compression.py` in BILD-VizionPipeline.

## Which recipes imitate the generic "iPhone look" from another camera, and what do they change?

### Takeaway
The generic recipes read in this pass agree on direction: a small warm shift, lifted shadows, compressed highlights, a mild saturation gain and sharpening that spares skin. Their inputs are AI images or a Xiaomi preview, so the magnitudes are not calibrated against Samsung or Pixel output.

### Cited Findings

| Adjustment | ivanprimestars default | BILD default | Direction |
| --- | --- | --- | --- |
| White balance | R x1.006, B x0.996 | R x1.009, G x1.003, B x0.988 (R 1.03, G 1.01, B 0.96 at full bias) | warmer |
| Shadows | up to +18% in dark regions | gamma 0.8 blended by 0.4 x (1 - luma)^2 | lifted |
| Highlights | knee 0.65, divide by up to 1.27 | knee 0.85, excess divided by 1.9 | compressed |
| Global contrast | local detail gain 1.18 | 7% mix toward image mean | flatter overall, more local detail |
| Saturation | x1.136 | not changed in the nodes read | slightly higher |
| Sharpening | unsharp 0.85 at sigma 1.0, less on skin | unsharp 0.6 at sigma 1.5, skin blurred | sharper textures, smoother skin |

- Table sources: [iphone_look_node.py](https://github.com/ivanprimestars/comfyui_iphone_look/blob/HEAD/iphone_look_node.py), [auto_white_balance.py](https://github.com/EllipsesMark/BILD-VizionPipeline/blob/HEAD/nodes/auto_white_balance.py), [smart_hdr.py](https://github.com/EllipsesMark/BILD-VizionPipeline/blob/HEAD/nodes/smart_hdr.py), [deep_fusion.py](https://github.com/EllipsesMark/BILD-VizionPipeline/blob/HEAD/nodes/deep_fusion.py)
- The BILD Smart HDR docstring states the assumed behaviour: shadows lifted, highlights compressed, overall contrast flattened. Source: [smart_hdr.py](https://github.com/EllipsesMark/BILD-VizionPipeline/blob/HEAD/nodes/smart_hdr.py)
- Unopened leads seen only as search snippets:
  - A Reddit answer says Samsung "bakes a ton of extra contrast and saturation" and advises a lower-contrast edit. Source: [r/AskPhotography thread](https://www.reddit.com/r/AskPhotography/comments/17znrv4/making_samsungs_photos_achieve_the_iphone_look/)
  - A Samsung Community post gives camera settings for an "iPhone vivid" skin tone: colour tone warm, warm filter at intensity 4, skin smoothness 2 to 4. Source: [Samsung Community](https://r2.community.samsung.com/t5/Galaxy-S/This-is-how-you-can-take-photos-Skin-tone-like-iPhone-vivid/td-p/15750529)
  - Cult of Mac reports that the XS renders with lower contrast and less saturation than earlier iPhones. Source: [Cult of Mac](https://www.cultofmac.com/606442/fix-iphone-xs-flat-photos/)

### Inferences
- A starting global model consistent with these recipes: R gain +0.5% to +3%, B gain -0.5% to -4%, shadow gamma near 0.8 limited to dark tones, a highlight knee between 0.65 and 0.85, saturation +0% to +14%. These bounds come from two unvalidated code recipes.
- Both ComfyUI recipes aim at making AI images look like phone photos. Their noise, JPEG and demosaic steps are irrelevant to a camera app.

### Gaps
- The Reddit, Samsung Community and Cult of Mac pages above were not opened. Their numbers are unverified.
- No Fujifilm, Sony or Pixel recipe that targets an iPhone look was found. A search for a Fujifilm recipe returned only recipe apps.
- LMC 8.4 or GCam XML configs named after iPhone models: only low-quality aggregator pages came up, no XML file was obtained.
- Paid and unverified: Tom Noske iPhone LUTs (29 USD, https://tomnoske.gumroad.com/l/iphoneluts) are looks for iPhone footage, not an imitation.

## Are there reverse-engineering write-ups of Apple's rendering?

### Takeaway
Three sources give real numbers: Apple's HDR gain map formula, the tone curve and local gain table inside an iPhone 12 Pro ProRAW file, and the Shalielie analysis of the iPhone 16+ style data. The style data is per-scene (regional polynomials, a tone curve and a delta map), so no fixed global parameter set per style is published.

### Cited Findings

Apple HDR gain map (HEIC, iPhone 12 and later, before the ISO gain map of iOS 18):
- Headroom from MakerNote keys 33 and 48: if `maker33 < 1.0` then `stops = -20.0 * maker48 + 1.8` when `maker48 <= 0.01`, else `stops = -0.101 * maker48 + 1.601`. Otherwise `stops = -70.0 * maker48 + 3.0` when `maker48 <= 0.01`, else `stops = -0.303 * maker48 + 2.303`. `headroom = 2^max(stops, 0)`. Source: [Apple developer documentation](https://developer.apple.com/documentation/appkit/applying-apple-hdr-effect-to-your-photos)
- Application in linear light: `hdr_rgb = sdr_rgb * (1.0 + (headroom - 1.0) * gainmap)`. The gain map is 1/4 resolution, encoded with the Rec.709 transfer function, auxiliary type `urn:com:apple:photo:2020:aux:hdrgainmap`. Source: [Apple developer documentation](https://developer.apple.com/documentation/appkit/applying-apple-hdr-effect-to-your-photos)
- A comment on a gain map gist reports `HDRGainMapHeadroom` 5.651999 in the XMP of an iPhone 15 Pro sample. Read from a fetch summary. Source: [kiding gist](https://gist.github.com/kiding/fa4876ab4ddc797e3f18c71b3c2eeb3a)
- For the ISO gain map introduced in iOS 18 the MakerNote headroom method does not apply. Source: [JuniperPhoton, 2025-04-16](https://juniperphoton.substack.com/p/decoding-some-hidden-magic-of-makerapple)

ProRAW (one iPhone 12 Pro file, iOS 14.3, 2020-12-29, parsed locally):
- ProfileName "Apple Embedded Color Profile". ProfileToneCurve has 257 points, linear input to linear output. Source: [IMG_1361.DNG](https://raw.pixls.us/data/Apple/iPhone%2012%20Pro/IMG_1361.DNG)

| Linear in | 0.01 | 0.02 | 0.05 | 0.10 | 0.18 | 0.25 | 0.35 | 0.50 | 0.65 | 0.75 | 0.90 | 0.95 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Linear out | 0.0001 | 0.0007 | 0.0095 | 0.0467 | 0.1221 | 0.1973 | 0.3147 | 0.5047 | 0.6828 | 0.7862 | 0.9223 | 0.9624 |

- The same curve on sRGB-encoded values: 0.10 gives 0.002, 0.18 gives 0.017, 0.25 gives 0.100, 0.35 gives 0.240, 0.50 gives 0.434, 0.65 gives 0.628, 0.75 gives 0.757, 0.90 gives 0.917, 0.95 gives 0.961. Slope near 0.5 is 1.28. Source: [IMG_1361.DNG](https://raw.pixls.us/data/Apple/iPhone%2012%20Pro/IMG_1361.DNG)
- ProfileGainTableMap (tag 52525, in the raw SubIFD, big-endian): 6 x 8 cells (vertical x horizontal), 257 table points, MapInputWeights for R, G, B, min, max = 0.02618, 0.08806, 0.00889, 0.0, 0.12312 (sum 0.24625). Source: [IMG_1361.DNG](https://raw.pixls.us/data/Apple/iPhone%2012%20Pro/IMG_1361.DNG)

| Table input | 0 to 0.016 | 0.031 | 0.0625 | 0.125 | 0.25 | 0.5 | 1.0 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| Mean gain over 48 cells | 3.07 | 2.68 | 2.19 | 1.73 | 0.971 | 0.486 | 0.243 |
| Min to max across cells | 2.18 to 4.26 | 1.63 to 3.84 | 1.35 to 2.95 | 1.32 to 1.92 | equal | equal | equal |

- Lux describes the Profile Gain Table Map as the Smart HDR local tone mapping stored as separate data that an editor can reduce or turn off. The article names the iPhone 12 Pro and gives no curve numbers. Source: [Ben Sandofsky, Lux, 2020-12-15](https://lux.camera/understanding-proraw/)

iPhone 16 and later Photographic Styles (Shalielie `facts.md`, author nathanatgit, MIT, created 2026-08-16, pushed 2026-11-04, based on 30 native files from iPhone 16, 17 and 18 on iOS 18.2, 26.5 and 27). Source for all items below: [Shalielie facts.md](https://github.com/nathanatgit/Shalielie/blob/master/facts.md)
- Styles plist key `1`: 51,840 bytes of FP16 = 2 x 18 x 24 x 10 x 3, a per-region second-order RGB polynomial with terms `[1, R, G, B, R^2, G^2, B^2, RG, RB, GB]`. About 93% of the bytes differ between two shots.
- Key `3`: a global tone curve of 256 u16 values after a 4-byte header. It differs per scene.
- Keys `c` and `d`: 32 x 32 FP16 light maps. Fits on eight native files: `c = clamp(0.7774 * L + 0.0294)`, `d = clamp(0.6542 * L - 0.0128)`, floor 0.040741, with L the linear Rec.709 luma. Leave-one-out MAE 0.022 and 0.037.
- Key `6`: scene statistics (black point at 0.1%, p02, p10, p25, p50, p75, p98, white point at 99.9%) of linear display luma, and the same values times 0.166 for `LinearImage`. `highKey` ranges 0.52 to 0.92.
- Style delta map: 10-bit RGB centred on 512, about 470 to 560. The author models the result as `styled = F(image, style) + D(x, y)`.
- MakerNote tag `0x54`: an 8-key record `{0:1, 1:0.0, 2:0.0, 3:1.0, 4:1, 5:1, 6:4, 7:0}` in photos shot with the pad untouched. Members 1 and 2 range -0.84 to 0.99 in Apple's demo shots, which the author reads as the Tone and Color pad position.
- Key `i.Gain`: 14.6 on iOS 18.2, 8.4 to 9.8 on iOS 26.5, 7.7 to 10.6 on iOS 27. Key `h` equals `Gain / 4` in 30 of 30 files.
- The port writes identity coefficients and an identity tone curve. The author lists these neutral defaults as the likely reason a ported photo looks different from a native one.

### Inferences
- In the 12 Pro file a white pixel gives table input 0.246 (the weight sum), where the gain is about 0.97. Gains above 1 apply only to darker pixels, and the 48 cells differ only below a table input of about 0.2. So the local tone map in this file is a shadow lift of 1.3 to 4.3 times in linear light with highlights left near 1.
- Illustration for neutral grey, assuming the gain is applied before the tone curve and ignoring BaselineExposure (order not checked against the DNG specification): linear 0.064 gives 0.14, 0.127 gives 0.30, 0.254 gives 0.57, 0.508 gives 0.90, 1.0 gives 0.98. This is one scene and uses the mean over cells.
- The ProfileToneCurve alone darkens shadows strongly (sRGB 0.25 gives 0.10). It is designed to follow the gain table and must not be used alone as "the iPhone tone curve".
- For iPhone 16 and later, a style is rendered from per-scene data. A fixed matrix or LUT per style can only be obtained by measuring rendered output pairs, not from the file metadata.

### Gaps
- No ProRAW sample for 13 Pro, 14 Pro, 15 Pro or 16 Pro was found, so the tone curve could not be compared across generations. raw.pixls.us has only the six Apple models listed above.
- The order in which the DNG pipeline applies BaselineExposure, ProfileGainTableMap and ProfileToneCurve was not checked in the DNG 1.6 specification.
- The raw.pixls.us licence statement (CC0 by site policy) was not re-read in this pass.
- No published numeric meaning for the Tone, Warmth, Color and Palette sliders was found beyond the Cassie Spiral analysis already documented.
- No published default Camera tone curve for JPEG or HEIC output and no Smart HDR parameter set was found for any model.
- Not opened: Shalielie `README.md` (30 KB), `tozn607/photographicstyles-v3` (no licence, core shipped as a prebuilt static library), `royzrh/apple` (same description as Shalielie).

## Which sources compare several iPhone generations with the same recipe framework?

### Takeaway
No new preset pack with separate looks per iPhone generation was found. The only same-framework comparisons from this pass are Apple's own DNG tags across five sensor generations and the Shalielie comparison of iPhone 16, 17 and 18 style files.

### Cited Findings
- Apple DNG ColorMatrix2 across 6s Plus, 7 Plus, 8, XS and 12 Pro: see the table under the first question. The 6s Plus and SE 1st gen samples have identical matrices. Source: [raw.pixls.us Apple folder](https://raw.pixls.us/data/Apple/)
- Shalielie compares native style files across iPhone 16, 17 and 18. The style data layout is the same, with schema 14 on iOS 18.2, 15 on iOS 26.5 and 16 on iOS 27. iPhone 18 files add a `texture_styles` record and twelve extra mattes for Texture and Grain. Source: [Shalielie facts.md](https://github.com/nathanatgit/Shalielie/blob/master/facts.md)
- The Shalielie author reports that changing `HardwareModel` from `iPhone19,2` to the iPhone 15 Pro value makes white areas glow under some styles, and concludes the field "likely selects render parameters". Source: [Shalielie facts.md](https://github.com/nathanatgit/Shalielie/blob/master/facts.md)

### Inferences
- The Pondowner857 table (already documented) remains the only open source with one parameter row per iPhone generation, and it has no validation and no licence.
- The `HardwareModel` observation is the only evidence found that Apple's renderer holds per-device render parameters. The parameters themselves are not published.

### Gaps
- Raztrend's per-generation profiles remain paid and unverified.
- No DxOMark-style measured colour or tone data per model was collected in this pass (out of scope here).

## Handoff

Done:
- Read `docs/PUBLIC_COLOR_RECIPES.md` and skipped its items.
- GitHub repository search (unauthenticated API) for: "iphone look lut", "iphone color science", "iphone filter lut", "photographic styles iphone", "iphone lut cube", "apple log lut", "iphone look comfyui", "iphone filter", "iphone camera filter python", "ios camera filter lut".
- GitHub code search with `gh`: `"iphone" --extension cube`, `--filename iphone --extension cube`, `"iphone" "crs:Saturation" --extension xmp`, `"iPhone" "crs:Temperature"`.
- Read and recorded: ivanprimestars node, three BILD nodes, Camera Style Pad `Style.java`, Shalielie `facts.md`, `iPhone-Looks-8.cube`, `iPhone11 - Film.xmp`, six raw.pixls.us DNG files, Apple gain map documentation.
- General web searches in English, Korean, Japanese and Chinese for value lists. They returned no post with numbers.

Remaining, in priority order:
1. Open these threads and copy any numbers: https://www.reddit.com/r/AskPhotography/comments/17znrv4/making_samsungs_photos_achieve_the_iphone_look/ , https://www.reddit.com/r/GalaxyS23Ultra/comments/111k41q/set_s23ultra_camera_to_look_like_iphone/ , https://www.reddit.com/r/postprocessing/comments/194xmuw/how_to_make_photos_look_like_they_were_taken_in/ , https://r2.community.samsung.com/t5/Galaxy-S/This-is-how-you-can-take-photos-Skin-tone-like-iPhone-vivid/td-p/15750529 , https://eu.community.samsung.com/t5/galaxy-s24-series/pro-mode-to-emulate-pixel-or-iphone-photos/td-p/9355677 , https://www.cultofmac.com/606442/fix-iphone-xs-flat-photos/ . The Reddit JSON API returns 403 from this machine, so use WebFetch or a browser.
2. Find ProRAW samples for 13 Pro to 16 Pro and run the DNG script on them. Leads not opened: https://exiftool.org/forum/index.php?msg=64410 , https://exiftool.org/forum/index.php?msg=75150 , https://support.captureone.com/hc/es/community/posts/6508633494045-Apple-Pro-Raw-DNG-files-are-underexposed . Search query: `ProRAW sample DNG "iPhone 15 Pro" download`.
3. Check the DNG pipeline order for ProfileGainTableMap in the DNG specification. Tag reference: https://helpx.adobe.com/photoshop/kb/dng-specification-tags.html . Code reference not opened: https://docs.rs/crate/zenraw/0.1.2/source/src/dng_render.rs .
4. Read the two tutorials' settings frames with a browser tool: https://www.youtube.com/watch?v=gSpbaOQgxrc (7:32) and https://www.youtube.com/watch?v=-b02h4UPapo .
5. Site searches not yet run: `site:lemon8-app.com iphone xs preset lightroom`, `site:lemon8-app.com iphone 6 look lightroom`, `site:pinterest.com iphone 6s lightroom preset settings`, Korean `갤럭시 아이폰 감성 보정법 라이트룸`, Chinese `安卓 仿苹果 原相机 调色参数`.
6. Not opened in the repositories: `/tmp/iphone_color_dl/shalielie/README.md`, https://github.com/tozn607/photographicstyles-v3 (README and `rust/examples/wb_probe.rs`, `wb_bands_probe.rs`, `styles_ps3_probe.rs`), https://github.com/EllipsesMark/BILD-VizionPipeline/blob/HEAD/nodes/authenticate_all.py .
7. Gain map decoders not opened: https://github.com/m13253/heif-hdrgainmap-decode/blob/master/README.md , https://github.com/grapeot/AppleJPEGGainMap , https://pypi.org/project/apple-hdr-heic , https://jacksblog-1j5.pages.dev/posts/iphone-heic-hdr-format .
8. Other leads: Hugging Face models search https://huggingface.co/api/models?search=iphone&limit=50 , Nothing preset library https://nothing.community/d/41181-unofficial-nothing-phone-preset-library-qr-codes , LMC configs https://t.me/s/POCO_F5_XML?before=209 , https://magichour.ai/products/color-grader/iphone-looks .
9. Pin commit hashes for cited GitHub files: `gh api repos/OWNER/REPO/commits/HEAD --jq .sha` prints the hash to the terminal.
10. Photoshop 2026 is installed on this Mac, but `/Library/Application Support/Adobe/CameraRaw/CameraProfiles/` was empty or absent. The location of Adobe's Apple camera profiles was not searched further.

Temporary files (outside the repository, may be removed on reboot):
- Scripts: `/tmp/iphone_color_scripts/` with `gh_search.py`, `dl.py`, `get.py`, `ddg.py`, `dng_dump.py`, `dng_dump2.py`, `cube_probe.py`.
- Downloads: `/tmp/iphone_color_dl/` with `shalielie/`, `stylepad/`, `comfy_iphone_look/`, `bild/`, `dng/`, `creatormarket_lut/`, `fredrik_xmp/`.
- Probe a LUT (prints a table to the terminal): `/opt/anaconda3/bin/python3 -I /tmp/iphone_color_scripts/cube_probe.py /tmp/iphone_color_dl/creatormarket_lut/iPhone-Looks-8.cube`
- Dump DNG colour tags (prints to the terminal): `/opt/anaconda3/bin/python3 -I /tmp/iphone_color_scripts/dng_dump2.py /tmp/iphone_color_dl/dng/iphone12pro_IMG_1361.DNG`
- `ddg.py` hits a rate-limit page after two queries in a row. Wait before the next call.
