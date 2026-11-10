# Measured ColorChecker results for nine iPhone models (Notebookcheck charts)

Measured on 2026-11-10 from files downloaded from notebookcheck.net. All nine exact models have a Notebookcheck review with a ColorChecker Passport chart, so no sibling model was needed. Status of each section is stated in "Handoff" at the end.

## Q1. Review page and chart image URL per model

### Takeaway
All nine models (6s, 7, 8, X, XS, 11, 13, 15 Pro, 16) have their own Notebookcheck review and chart image, and every original image file downloaded with HTTP 200. Two layouts exist: 2015 to 2018 reviews burn the reference colour into the lower half of each patch, 2019 to 2024 reviews publish the plain phone photo and draw the reference as an HTML overlay.

### Cited Findings

| Model | Review page | Original chart image | Layout, file facts (read from the file on 2026-11-10) |
| --- | --- | --- | --- |
| 6s | [review](https://www.notebookcheck.net/Apple-iPhone-6S-Smartphone-Review.151403.0.html) | [image](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_6S/ColorChecker_colors_small_iphone6s.jpg) | Composite, 1613x1080, no ICC profile, no EXIF |
| 7 | [review](https://www.notebookcheck.net/Apple-iPhone-7-Smartphone-Review.173851.0.html) | [image](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_7/Foto_iPhone7_ColorChecker_Compare.jpg) | Composite, 2030x1353, Display P3, EXIF "iPhone 7 back camera 3.99mm f/1.8", 2016-09-16 |
| 8 | [review](https://www.notebookcheck.net/Apple-iPhone-8-Smartphone-Review.250930.0.html) | [image](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_8/Calman.jpg) | Composite, 3124x2116, Display P3, EXIF "iPhone 8 back camera 3.99mm f/1.8", 2017-09-22 |
| X | [review](https://www.notebookcheck.net/Apple-iPhone-X-Smartphone-Review.264436.0.html) | [image](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_X/colorchecker_comparison_iphone_x.jpg) | Composite inside a full photo of the Passport, 2000x1500, no ICC profile, no EXIF |
| XS | [review](https://www.notebookcheck.net/Apple-iPhone-XS-Smartphone-Review.333546.0.html) | [image](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_Xs/Foto_iPhoneXS_ColorChecker.jpg) | Composite, 2995x1997, Display P3, EXIF "iPhone XS back dual camera 4.25mm f/1.8", 2018-09-22 |
| 11 | [review](https://www.notebookcheck.net/Apple-iPhone-11-Smartphone-Review-More-than-just-an-affordable-Apple-smartphone.435087.0.html) | [image](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_11/Foto_iPhone_11_ColoChecker.jpg) | Plain photo plus HTML overlay, 2686x1791, Display P3, EXIF "iPhone 11 back dual wide camera 4.25mm f/1.8", 2019-09-20 |
| 13 | [review](https://www.notebookcheck.net/Apple-iPhone-13-Smartphone-Review-High-quality-Apple-phone-with-an-even-brighter-display.572153.0.html) | [image](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_13/Farbchart_high.jpg) | Plain photo plus HTML overlay, 2215x1457, Display P3, EXIF "iPhone 13 back dual wide camera 5.1mm f/1.6", 2021-10-03 |
| 15 Pro | [review](https://www.notebookcheck.net/Apple-iPhone-15-Pro-smartphone-in-review-High-end-phone-as-a-replacement-for-the-PlayStation.761070.0.html) | [image](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_15_Pro/Farbchart_high.jpg) | Plain photo plus HTML overlay, 1516x1003, Display P3, EXIF "iPhone 15 Pro back triple camera 2.22mm f/2.2", digital zoom 1.82, 2023-10-18 |
| 16 | [review](https://www.notebookcheck.net/Apple-iPhone-16-smartphone-review-More-innovations-than-one-would-think.908118.0.html) | [image](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_16/Foto_iPhone16_ColorChecker.jpg) | Plain photo plus HTML overlay, 3064x2043, Display P3, EXIF "iPhone 16 back dual wide camera 5.96mm f/1.6", 2024-09-24 |

- Low light variants exist for four models and were also downloaded: [11 at 1 lux](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_11/Foto_iPhone_11_ColorChecker_1Lux.jpg), [13 low](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_13/Farbchart_low.jpg), [15 Pro low](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_15_Pro/Farbchart_low.jpg), [16 at 1 lux](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_16/Foto_iPhone16_ColorChecker_1Lux.jpg).
- The 15 Pro chart was not shot with the main camera. Its EXIF lens is the 2.22 mm f/2.2 ultra wide module with digital zoom 1.82 (the high light and the low light file both), which is what the iPhone macro auto switch produces at close range ([15 Pro chart file](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_15_Pro/Farbchart_high.jpg)).
- The images shown inline on the review pages are resized copies with the ICC profile removed but the pixel numbers unchanged (mean absolute difference to the original 0.02 to 0.43 on a 0 to 255 scale, measured on the 7, 8, XS, 11, 13, 15 Pro, 16 files). Example: [inline 16 copy](https://www.notebookcheck.net/fileadmin/_processed_/2/3/csm_Foto_iPhone16_ColorChecker_f64e3f45a4.jpg). Anyone sampling an inline copy must treat it as Display P3, not sRGB.
- For 11, 13, 15 Pro and 16 the review page HTML carries the reference colour and Notebookcheck's own Delta E for each patch in `colorchk_box` elements. Site means for the normal light chart: 11 = 10.1, 13 = 11.2, 15 Pro = 8.95, 16 = 9.58 ([11](https://www.notebookcheck.net/Apple-iPhone-11-Smartphone-Review-More-than-just-an-affordable-Apple-smartphone.435087.0.html), [13](https://www.notebookcheck.net/Apple-iPhone-13-Smartphone-Review-High-quality-Apple-phone-with-an-even-brighter-display.572153.0.html), [15 Pro](https://www.notebookcheck.net/Apple-iPhone-15-Pro-smartphone-in-review-High-end-phone-as-a-replacement-for-the-PlayStation.761070.0.html), [16](https://www.notebookcheck.net/Apple-iPhone-16-smartphone-review-More-innovations-than-one-would-think.908118.0.html)).

### Inferences
- The site's Delta E numbers are CIEDE2000 computed on the stored pixel numbers read as sRGB, ignoring the Display P3 profile. Recomputing that way reproduces the site means within 0.05 (11: 10.07, 13: 11.15, 15 Pro: 8.95, 16: 9.58) and the per patch values with a mean absolute error of 0.21 to 0.31. With the profile applied the CIEDE2000 means are 10.84, 12.06, 9.67, 10.62.

### Gaps
- GSMArena photo compare charts and the DPReview studio scene were not opened. See Handoff.

<!-- AUTO-TABLES-BEGIN -->
## Q2. Average sRGB values of the phone half and the reference half, all 24 patches

### Takeaway
PENDING

### Cited Findings
Table 2a. Phone colour per patch as sRGB R,G,B (0 to 255). Mean of the centre window of the phone half (composites) or of the patch (plain photos). Files with a Display P3 profile were converted to sRGB (D65, matrix conversion); a trailing * marks a colour outside the sRGB gamut, shown clipped. The X file has no profile and is read as sRGB.

| # | Patch | Reference | 6s | 7 | 8 | X | XS | 11 | 13 | 15 Pro | 16 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | dark skin | 115,82,68 | 103,70,56 | 111,77,65 | 163,106,91 | 145,87,67 | 185,123,94 | 184,132,106 | 161,115,83 | 111,89,64 | 164,107,82 |
| 2 | light skin | 194,150,130 | 218,153,124 | 223,154,133 | 255,172,149* | 239,163,140 | 253,168,140 | 249,184,161 | 253,185,152 | 231,185,154 | 249,179,150 |
| 3 | blue sky | 98,122,157 | 87,117,156 | 93,125,169 | 102,140,193 | 104,145,193 | 120,162,212 | 124,173,228 | 108,158,224 | 97,148,196 | 113,158,213 |
| 4 | foliage | 87,108,67 | 83,112,59 | 88,123,68 | 101,147,73 | 120,143,81 | 122,171,78 | 132,178,82 | 120,157,60 | 85,120,59 | 123,146,70 |
| 5 | blue flower | 133,128,177 | 129,122,175 | 139,131,193 | 161,147,220 | 162,151,211 | 176,160,227 | 168,165,234 | 158,158,241 | 146,163,227 | 167,160,232 |
| 6 | bluish green | 103,189,170 | 95,190,158 | 100,205,181 | 100,232,208 | 116,219,197 | 99,225,190 | 113,219,196 | 106,239,208 | 107,237,213 | 122,230,211 |
| 7 | orange | 214,126,44 | 229,122,52 | 239,124,51 | 255,154,49* | 252,118,19 | 255,129,12* | 253,137,16 | 255,154,0* | 247,146,0* | 255,134,0* |
| 8 | purplish blue | 80,91,166 | 54,66,158 | 70,80,180 | 62,78,197 | 67,87,196 | 84,98,223 | 85,114,232 | 55,86,251 | 61,99,207 | 85,114,230 |
| 9 | moderate red | 193,90,99 | 219,86,88 | 226,82,95 | 246,75,88 | 243,77,93 | 255,91,89* | 255,98,107* | 255,92,105* | 225,99,109 | 255,85,95 |
| 10 | purple | 94,60,108 | 70,43,85 | 74,47,90 | 80,46,98 | 109,60,116 | 120,60,131 | 115,75,148 | 108,61,147 | 75,55,108 | 120,67,135 |
| 11 | yellow green | 157,188,64 | 160,206,60 | 162,215,61 | 162,217,23 | 178,217,62 | 165,229,1 | 175,226,0* | 187,237,0* | 170,227,0* | 193,233,0* |
| 12 | orange yellow | 224,163,46 | 226,171,45 | 240,182,44 | 255,189,0* | 252,166,11 | 255,180,0* | 242,185,0* | 255,206,0* | 246,194,0* | 255,182,0* |
| 13 | blue | 56,61,150 | 27,32,116 | 42,47,141 | 44,60,215 | 22,52,171 | 47,50,204 | 45,69,206 | 25,11,230 | 32,71,199 | 36,68,212 |
| 14 | green | 70,148,73 | 69,143,57 | 69,161,69 | 50,198,74 | 55,174,66 | 60,197,75 | 77,189,61 | 60,197,25 | 85,193,69 | 78,187,56 |
| 15 | red | 175,54,60 | 179,62,55 | 186,62,61 | 219,51,47 | 226,45,54 | 234,60,47 | 224,62,54 | 233,47,49 | 214,61,56 | 242,47,56 |
| 16 | yellow | 231,199,31 | 236,209,41 | 244,221,24 | 253,229,0* | 255,219,0 | 255,220,0* | 253,230,0* | 255,239,0* | 247,231,0* | 255,221,0* |
| 17 | magenta | 187,86,149 | 195,75,136 | 210,79,158 | 200,59,135 | 226,72,149 | 242,67,153 | 236,87,184 | 251,81,194 | 209,101,172 | 253,85,181 |
| 18 | cyan | 8,133,161 | 29,119,153 | 7,136,180 | 0,121,170* | 0,148,188 | 0,149,195* | 0,156,211* | 0,164,231* | 24,156,198 | 0,167,223* |
| 19 | white 9.5 | 243,243,242 | 235,224,203 | 249,240,229 | 255,255,255 | 255,255,255 | 242,232,209 | 231,231,222 | 247,251,236 | 249,253,230 | 242,243,235 |
| 20 | neutral 8 | 200,200,200 | 200,194,182 | 210,208,204 | 255,255,255 | 217,216,212 | 215,211,197 | 206,208,202 | 223,229,221 | 231,237,217 | 223,224,220 |
| 21 | neutral 6.5 | 160,160,160 | 162,159,149 | 174,174,171 | 211,213,210 | 180,181,176 | 192,189,176 | 187,190,189 | 193,202,194 | 200,208,188 | 204,206,203 |
| 22 | neutral 5 | 122,122,121 | 118,113,107 | 127,127,126 | 146,146,144 | 141,140,136 | 149,142,133 | 151,152,152 | 151,157,150 | 132,136,120 | 160,161,156 |
| 23 | neutral 3.5 | 85,85,85 | 76,76,73 | 76,78,79 | 75,75,75 | 105,105,105 | 96,92,87 | 103,106,108 | 98,105,100 | 75,80,73 | 107,107,107 |
| 24 | black 2 | 52,52,52 | 41,42,42 | 34,36,38 | 29,32,31 | 39,40,39 | 47,48,43 | 57,58,60 | 43,45,44 | 26,28,23 | 59,59,61 |

Sources: [6s](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_6S/ColorChecker_colors_small_iphone6s.jpg), [7](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_7/Foto_iPhone7_ColorChecker_Compare.jpg), [8](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_8/Calman.jpg), [X](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_X/colorchecker_comparison_iphone_x.jpg), [XS](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_Xs/Foto_iPhoneXS_ColorChecker.jpg), [11](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_11/Foto_iPhone_11_ColoChecker.jpg), [13](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_13/Farbchart_high.jpg), [15 Pro](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_15_Pro/Farbchart_high.jpg), [16](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_16/Foto_iPhone16_ColorChecker.jpg).

Table 2b. Reference half as measured in the five composite images (sRGB R,G,B). 6s, 7, X: stored numbers. 8, XS: stored Display P3 numbers converted to sRGB.

| # | Standard | 6s | 7 | 8 | X | XS |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | 115,82,68 | 115,82,67 | 115,82,67 | 115,82,69 | 115,82,67 | 115,82,69 |
| 2 | 194,150,130 | 195,150,131 | 195,150,131 | 194,150,130 | 195,150,131 | 194,150,130 |
| 3 | 98,122,157 | 98,122,158 | 98,122,158 | 98,122,157 | 98,122,158 | 98,122,157 |
| 4 | 87,108,67 | 87,108,67 | 87,108,67 | 87,108,67 | 87,108,67 | 87,108,67 |
| 5 | 133,128,177 | 134,127,178 | 134,127,178 | 132,128,177 | 134,127,178 | 132,128,177 |
| 6 | 103,189,170 | 104,189,170 | 104,189,170 | 103,189,170 | 104,189,170 | 103,189,170 |
| 7 | 214,126,44 | 215,126,44 | 215,126,44 | 213,127,42 | 215,126,44 | 213,127,42 |
| 8 | 80,91,166 | 79,91,165 | 79,91,165 | 80,91,167 | 79,91,165 | 80,91,167 |
| 9 | 193,90,99 | 194,90,99 | 194,90,99 | 194,90,99 | 194,90,99 | 194,90,99 |
| 10 | 94,60,108 | 94,61,108 | 94,61,108 | 94,60,108 | 94,61,108 | 94,60,108 |
| 11 | 157,188,64 | 158,188,64 | 158,188,64 | 156,188,66 | 158,188,64 | 156,188,66 |
| 12 | 224,163,46 | 224,163,46 | 224,163,46 | 224,163,48 | 224,163,46 | 224,163,48 |
| 13 | 56,61,150 | 56,62,150 | 56,62,150 | 55,61,150 | 56,62,150 | 55,61,150 |
| 14 | 70,148,73 | 70,148,73 | 70,148,73 | 70,148,73 | 70,148,73 | 70,148,73 |
| 15 | 175,54,60 | 175,54,61 | 175,54,61 | 175,55,61 | 175,54,61 | 175,55,61 |
| 16 | 231,199,31 | 231,198,31 | 231,198,31 | 230,199,31 | 231,198,31 | 230,199,31 |
| 17 | 187,86,149 | 186,86,150 | 186,86,150 | 187,86,149 | 186,86,150 | 187,86,149 |
| 18 | 8,133,161 | 8,133,161 | 8,133,161 | 13,133,162 | 8,133,161 | 13,133,162 |
| 19 | 243,243,242 | 243,243,243 | 243,243,243 | 243,243,243 | 243,243,243 | 243,243,243 |
| 20 | 200,200,200 | 200,200,200 | 200,200,200 | 200,200,200 | 200,200,200 | 200,200,200 |
| 21 | 160,160,160 | 160,160,160 | 160,160,160 | 160,160,160 | 160,160,160 | 160,160,160 |
| 22 | 122,122,121 | 122,122,122 | 122,122,122 | 122,122,122 | 122,122,122 | 122,122,122 |
| 23 | 85,85,85 | 85,85,85 | 85,85,85 | 85,85,85 | 85,85,85 | 85,85,85 |
| 24 | 52,52,52 | 52,52,52 | 52,52,52 | 52,52,52 | 52,52,52 | 52,52,52 |

Table 2c. Fit of the measured reference halves to the standard ColorChecker Classic sRGB values.

| Model | Mean abs error (0 to 255) | Max abs error | Mean dE76 |
| --- | --- | --- | --- |
| 6s | 0.29 | 1.0 | 0.43 |
| 7 | 0.29 | 1.0 | 0.43 |
| 8 | 0.39 | 5.1 | 0.37 |
| X | 0.29 | 1.0 | 0.43 |
| XS | 0.39 | 5.2 | 0.37 |

Sources: [6s](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_6S/ColorChecker_colors_small_iphone6s.jpg), [7](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_7/Foto_iPhone7_ColorChecker_Compare.jpg), [8](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_8/Calman.jpg), [X](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_X/colorchecker_comparison_iphone_x.jpg), [XS](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_Xs/Foto_iPhoneXS_ColorChecker.jpg).

### Inferences
PENDING

### Gaps
PENDING

## Q3. Delta E, saturation, hue shifts, white balance and tone per model

### Takeaway
PENDING

### Cited Findings
All values below are computed from Table 2a against the standard reference (CIELAB, D65 white). Chroma is C*ab, shown as phone divided by reference in percent (100 = same saturation). "After WB" means the phone values were first multiplied by linear gains that make neutral 8 to neutral 3.5 grey, which removes the colour cast before saturation and hue are compared.

Table 3a. Colour error and chroma ratio, as measured.

| Model | Mean dE76 | Mean dE00 | Max dE00 | Chroma % (mean of 18) | Chroma % (fit slope) | skin 1,2 | reds 9,15 | yellow/orange 7,12,16 | greens 4,11,14 | blues 3,8,13 | red 15 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 6s | 7.9 | 4.9 | 9.5 (white 9.5) | 112 | 108 | 127 | 116 | 103 | 117 | 114 | 101 |
| 7 | 8.6 | 4.6 | 7.0 (yellow green) | 118 | 115 | 123 | 121 | 110 | 124 | 122 | 105 |
| 8 | 19.0 | 9.2 | 15.1 (green) | 144 | 140 | 165 | 150 | 130 | 155 | 157 | 139 |
| X | 13.9 | 7.1 | 14.1 (foliage) | 134 | 129 | 164 | 149 | 123 | 132 | 138 | 143 |
| XS | 21.5 | 10.6 | 23.1 (foliage) | 153 | 148 | 181 | 159 | 137 | 168 | 156 | 147 |
| 11 | 20.2 | 10.9 | 25.3 (foliage) | 146 | 144 | 144 | 143 | 138 | 174 | 150 | 136 |
| 13 | 26.6 | 12.1 | 19.9 (foliage) | 167 | 163 | 156 | 155 | 143 | 186 | 202 | 149 |
| 15 Pro | 17.7 | 9.7 | 15.8 (neutral 6.5) | 131 | 138 | 109 | 124 | 148 | 147 | 140 | 127 |
| 16 | 21.6 | 10.6 | 16.1 (foliage) | 150 | 150 | 162 | 156 | 150 | 157 | 152 | 152 |
| X, if file were Display P3 | 22.1 | 8.6 | 14.6 (foliage) | 159 | 158 | 199 | 178 | 163 | 159 | 147 | 170 |

Sources: [6s](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_6S/ColorChecker_colors_small_iphone6s.jpg), [7](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_7/Foto_iPhone7_ColorChecker_Compare.jpg), [8](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_8/Calman.jpg), [X](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_X/colorchecker_comparison_iphone_x.jpg), [XS](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_Xs/Foto_iPhoneXS_ColorChecker.jpg), [11](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_11/Foto_iPhone_11_ColoChecker.jpg), [13](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_13/Farbchart_high.jpg), [15 Pro](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_15_Pro/Farbchart_high.jpg), [16](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_16/Foto_iPhone16_ColorChecker.jpg).

Table 3b. Chroma ratio after WB neutralisation. "C*/(L*+16)" also divides by lightness, so it is insensitive to exposure.

| Model | Chroma % (mean of 18) | C*/(L*+16) % | skin 1,2 | reds 9,15 | yellow/orange 7,12,16 | greens 4,11,14 | blues 3,8,13 | red 15 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 6s | 112 | 117 | 111 | 110 | 99 | 114 | 127 | 97 |
| 7 | 119 | 116 | 123 | 121 | 110 | 123 | 124 | 105 |
| 8 | 145 | 132 | 164 | 151 | 130 | 154 | 161 | 139 |
| X | 134 | 124 | 160 | 148 | 123 | 130 | 144 | 142 |
| XS | 155 | 135 | 157 | 151 | 137 | 165 | 178 | 140 |
| 11 | 147 | 124 | 149 | 145 | 139 | 173 | 152 | 138 |
| 13 | 173 | 147 | 164 | 162 | 147 | 183 | 215 | 155 |
| 15 Pro | 141 | 125 | 99 | 127 | 159 | 140 | 171 | 130 |
| 16 | 152 | 130 | 161 | 157 | 152 | 156 | 158 | 152 |
| X, if file were Display P3 | 161 | 147 | 195 | 177 | 165 | 158 | 153 | 170 |

Sources: [6s](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_6S/ColorChecker_colors_small_iphone6s.jpg), [7](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_7/Foto_iPhone7_ColorChecker_Compare.jpg), [8](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_8/Calman.jpg), [X](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_X/colorchecker_comparison_iphone_x.jpg), [XS](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_Xs/Foto_iPhoneXS_ColorChecker.jpg), [11](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_11/Foto_iPhone_11_ColoChecker.jpg), [13](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_13/Farbchart_high.jpg), [15 Pro](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_15_Pro/Farbchart_high.jpg), [16](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_16/Foto_iPhone16_ColorChecker.jpg).

Table 3c. Hue shift in degrees of CIELAB hue angle, phone minus reference, as measured / after WB neutralisation. Positive means: skin and red toward yellow, greens toward cyan, blues toward purple.

| Model | 1 dark skin | 2 light skin | 3 blue sky | 8 purplish blue | 13 blue | 4 foliage | 11 yellow green | 14 green | 15 red |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 6s | -0.4 / -4.6 | -1.4 / -5.6 | -1.0 / +3.1 | +3.9 / +4.5 | +2.8 / +3.2 | +0.5 / +3.5 | +2.8 / +4.2 | -4.3 / -2.8 | +6.3 / +4.2 |
| 7 | -3.9 / -5.3 | -8.8 / -10.1 | -0.2 / +1.0 | +3.7 / +4.0 | +1.8 / +1.9 | +3.5 / +3.5 | +4.0 / +3.8 | -1.5 / -1.4 | +3.1 / +2.8 |
| 8 | -8.0 / -9.4 | -11.3 / -12.8 | +1.1 / +2.5 | +4.5 / +4.8 | +3.1 / +3.3 | +3.2 / +3.3 | +2.0 / +1.9 | +0.3 / +0.5 | +7.7 / +7.5 |
| X | -4.5 / -5.7 | -8.3 / -10.4 | -3.5 / -1.4 | +2.5 / +2.9 | +0.9 / +1.2 | -5.2 / -4.7 | -0.0 / +0.1 | -0.6 / -0.3 | +4.6 / +4.1 |
| XS | +2.0 / -1.7 | -7.0 / -12.8 | -3.0 / +2.5 | +3.9 / +4.7 | +4.8 / +5.1 | -0.3 / +2.4 | +2.8 / +3.8 | -0.4 / +0.9 | +10.0 / +8.7 |
| 11 | +3.6 / +2.0 | -5.1 / -6.8 | -3.8 / -2.4 | +0.1 / +0.4 | +0.7 / +0.9 | -2.1 / -2.7 | -2.4 / -3.0 | -4.8 / -4.9 | +7.8 / +7.8 |
| 13 | +11.2 / +4.7 | +1.4 / -6.3 | +1.3 / +6.3 | +6.1 / +6.8 | +8.2 / +8.2 | -5.8 / -7.7 | -3.3 / -5.3 | -6.9 / -7.1 | +6.8 / +6.5 |
| 15 Pro | +23.0 / +9.1 | +9.8 / -10.7 | -7.8 / +4.1 | -1.4 / +1.7 | -1.3 / +0.5 | +1.7 / +2.4 | +1.0 / -0.6 | -4.7 / -3.7 | +6.5 / +4.2 |
| 16 | +1.3 / -0.4 | -2.3 / -4.9 | -1.2 / +1.0 | -0.2 / +0.3 | +0.9 / +1.2 | -9.1 / -9.3 | -4.1 / -4.4 | -5.8 / -5.7 | +5.0 / +4.7 |
| X, if file were Display P3 | -6.7 / -7.7 | -10.9 / -12.8 | -7.8 / -5.3 | +2.2 / +2.6 | +0.7 / +1.0 | -3.7 / -3.2 | -0.3 / -0.3 | +2.6 / +2.8 | +5.4 / +5.1 |

Sources: [6s](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_6S/ColorChecker_colors_small_iphone6s.jpg), [7](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_7/Foto_iPhone7_ColorChecker_Compare.jpg), [8](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_8/Calman.jpg), [X](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_X/colorchecker_comparison_iphone_x.jpg), [XS](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_Xs/Foto_iPhoneXS_ColorChecker.jpg), [11](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_11/Foto_iPhone_11_ColoChecker.jpg), [13](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_13/Farbchart_high.jpg), [15 Pro](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_15_Pro/Farbchart_high.jpg), [16](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_16/Foto_iPhone16_ColorChecker.jpg).

Table 3d. White balance on the neutral patches (mean of neutral 8, 6.5, 5, 3.5; clipped patches left out). Ratios in percent deviation from 1. Linear = linear light sRGB, encoded = gamma encoded sRGB numbers. Tint = G against the mean of R and B (linear). CCT and Duv describe the rendered grey on a D65 display (6504 K and 0 would be neutral, lower CCT is warmer, positive Duv is greener).

| Model | R/G % linear | B/G % linear | R/G % encoded | B/G % encoded | Tint % | a* | b* | CCT K | Duv | Label | Patches used |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 6s | +5.2 | -11.3 | +2.3 | -5.4 | +3.1 | -0.1 | +4.5 | 5884 | +0.0041 | warm, green | 20,21,22,23 |
| 7 | -1.0 | -2.0 | -0.5 | -0.9 | +1.5 | -0.4 | +0.9 | 6460 | +0.0040 | mixed, no tint | 20,21,22,23 |
| 8 | -0.8 | -2.8 | -0.3 | -1.3 | +1.8 | -0.7 | +1.0 | 6415 | +0.0041 | mixed, no tint | 21,22,23 |
| X | +0.3 | -4.2 | +0.1 | -1.9 | +2.0 | -0.5 | +1.7 | 6323 | +0.0041 | mixed, green | 20,21,22,23 |
| XS | +6.4 | -13.9 | +2.9 | -6.6 | +3.9 | -0.4 | +6.0 | 5753 | +0.0043 | warm, green | 20,21,22,23 |
| 11 | -3.1 | -1.1 | -1.4 | -0.5 | +2.2 | -1.0 | +0.4 | 6580 | +0.0046 | mixed, green | 20,21,22,23 |
| 13 | -9.2 | -8.7 | -4.3 | -4.1 | +9.8 | -3.8 | +2.7 | 6479 | +0.0085 | mixed, green | 20,21,22,23 |
| 15 Pro | -7.9 | -20.0 | -3.8 | -9.8 | +16.3 | -4.9 | +7.4 | 5994 | +0.0110 | mixed, green | 20,21,22,23 |
| 16 | -1.6 | -3.9 | -0.7 | -1.8 | +2.8 | -1.1 | +1.5 | 6398 | +0.0047 | mixed, green | 20,21,22,23 |
| X, if file were Display P3 | +0.3 | -4.6 | +0.1 | -2.1 | +2.2 | -0.6 | +1.9 | 6304 | +0.0041 | mixed, green | 20,21,22,23 |

Sources: [6s](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_6S/ColorChecker_colors_small_iphone6s.jpg), [7](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_7/Foto_iPhone7_ColorChecker_Compare.jpg), [8](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_8/Calman.jpg), [X](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_X/colorchecker_comparison_iphone_x.jpg), [XS](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_Xs/Foto_iPhoneXS_ColorChecker.jpg), [11](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_11/Foto_iPhone_11_ColoChecker.jpg), [13](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_13/Farbchart_high.jpg), [15 Pro](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_15_Pro/Farbchart_high.jpg), [16](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_16/Foto_iPhone16_ColorChecker.jpg).

Table 3e. Encoded R/G % and B/G % for each neutral patch (R/G, B/G).

| Model | 19 white 9.5 | 20 neutral 8 | 21 neutral 6.5 | 22 neutral 5 | 23 neutral 3.5 | 24 black 2 |
| --- | --- | --- | --- | --- | --- | --- |
| 6s | +4.8, -9.3 | +3.1, -6.2 | +2.2, -5.8 | +4.3, -5.4 | -0.1, -4.3 | -1.9, -0.5 |
| 7 | +3.5, -4.6 | +0.7, -2.0 | -0.2, -1.9 | +0.1, -0.5 | -2.7, +0.8 | -6.0, +4.7 |
| 8 | -0.0, +0.0 | -0.0, +0.0 | -1.2, -1.6 | +0.0, -1.6 | +0.1, -0.7 | -7.4, -1.8 |
| X | +0.0, +0.0 | +0.5, -1.9 | -0.5, -2.9 | +0.7, -2.9 | -0.2, -0.1 | -0.1, -0.1 |
| XS | +4.4, -10.0 | +1.5, -6.6 | +1.5, -6.9 | +4.6, -6.6 | +4.1, -6.4 | -2.4, -11.4 |
| 11 | +0.1, -4.0 | -0.8, -2.6 | -1.5, -0.6 | -0.5, -0.2 | -3.0, +1.5 | -2.2, +3.7 |
| 13 | -1.5, -5.9 | -2.8, -3.4 | -4.4, -3.9 | -4.0, -4.6 | -6.1, -4.3 | -5.6, -2.6 |
| 15 Pro | -1.4, -9.0 | -2.3, -8.5 | -3.9, -9.5 | -2.4, -11.4 | -6.5, -9.8 | -8.0, -19.8 |
| 16 | -0.5, -3.6 | -0.6, -2.0 | -1.2, -1.8 | -0.4, -3.2 | -0.6, -0.2 | -0.0, +2.6 |
| X, if file were Display P3 | -0.0, +0.0 | +0.6, -2.0 | -0.7, -3.2 | +0.9, -3.1 | -0.2, -0.1 | -0.2, -0.1 |

Sources: [6s](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_6S/ColorChecker_colors_small_iphone6s.jpg), [7](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_7/Foto_iPhone7_ColorChecker_Compare.jpg), [8](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_8/Calman.jpg), [X](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_X/colorchecker_comparison_iphone_x.jpg), [XS](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_Xs/Foto_iPhoneXS_ColorChecker.jpg), [11](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_11/Foto_iPhone_11_ColoChecker.jpg), [13](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_13/Farbchart_high.jpg), [15 Pro](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_15_Pro/Farbchart_high.jpg), [16](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_16/Foto_iPhone16_ColorChecker.jpg).

Table 3f. Tone on the neutral patches. L* phone per patch (reference L* in the header). EV at N5 = log2 of phone luminance over reference luminance at neutral 5. Gamma = slope of log luminance, phone against reference, over the unclipped patches of neutral 8 to 3.5 (above 1 is more contrast). Residual = phone L* minus the L* the reference would have after the same EV shift (positive in the shadows is lift, negative in the highlights is compression).

| Model | white 9.5 (95.8) | neutral 8 (80.6) | neutral 6.5 (65.9) | neutral 5 (51.2) | neutral 3.5 (36.1) | black 2 (21.7) | EV at N5 | Gamma | L* slope N8 to black | Residual white | Residual N8 | Residual N3.5 | Residual black | Clipped patches |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 6s | 89.5 | 78.7 | 65.4 | 47.8 | 32.2 | 17.0 | -0.22 | 1.11 | 1.06 | -0.7 | +2.9 | -1.3 | -2.8 | none |
| 7 | 95.3 | 83.5 | 71.1 | 53.2 | 33.2 | 14.0 | +0.13 | 1.15 | 1.20 | -3.9 | -0.0 | -4.5 | -8.8 | none |
| 8 | 100.0 | 100.0 | 85.1 | 60.5 | 32.0 | 11.8 | +0.56 | 1.66 | 1.68 | -0.0 | +5.9 | -11.4 | -15.1 | 2,7,12,16,19,20 |
| X | 100.0 | 86.3 | 73.6 | 58.1 | 44.3 | 15.9 | +0.42 | 0.87 | 1.15 | +0.0 | -4.2 | +2.9 | -9.7 | 7,12,16,19 |
| XS | 92.1 | 84.6 | 76.6 | 59.5 | 39.4 | 19.6 | +0.50 | 0.99 | 1.13 | -7.9 | -7.9 | -3.1 | -6.7 | 2,7,9,12,16 |
| 11 | 91.3 | 83.0 | 76.7 | 62.8 | 44.8 | 24.5 | +0.69 | 0.81 | 1.01 | -8.7 | -14.2 | -0.4 | -3.7 | 7,9,16 |
| 13 | 97.9 | 90.2 | 80.5 | 64.0 | 43.6 | 18.2 | +0.76 | 0.95 | 1.23 | -2.1 | -8.8 | -2.5 | -10.7 | 2,7,8,9,12,16,17 |
| 15 Pro | 98.5 | 92.7 | 82.5 | 55.8 | 33.5 | 9.9 | +0.29 | 1.32 | 1.45 | -1.5 | +5.4 | -6.2 | -14.4 | 19 |
| 16 | 95.7 | 89.1 | 82.6 | 66.0 | 45.2 | 24.9 | +0.86 | 0.90 | 1.12 | -4.3 | -10.9 | -2.4 | -5.1 | 7,9,12,16,17 |
| X, if file were Display P3 | 100.0 | 86.3 | 73.5 | 58.1 | 44.3 | 15.9 | +0.42 | 0.86 | 1.15 | -0.0 | -4.2 | +2.9 | -9.7 | 2,7,9,12,16,19 |

Sources: [6s](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_6S/ColorChecker_colors_small_iphone6s.jpg), [7](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_7/Foto_iPhone7_ColorChecker_Compare.jpg), [8](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_8/Calman.jpg), [X](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_X/colorchecker_comparison_iphone_x.jpg), [XS](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_Xs/Foto_iPhoneXS_ColorChecker.jpg), [11](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_11/Foto_iPhone_11_ColoChecker.jpg), [13](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_13/Farbchart_high.jpg), [15 Pro](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_15_Pro/Farbchart_high.jpg), [16](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_16/Foto_iPhone16_ColorChecker.jpg).

Table 3g. Low light charts (supplementary, same method).

| Chart | Mean dE00 | Chroma % (mean of 18) | R/G % linear | B/G % linear | EV at N5 | Gamma |
| --- | --- | --- | --- | --- | --- | --- |
| [11 (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_11/Foto_iPhone_11_ColorChecker_1Lux.jpg) | 32.2 | 50 | -2.4 | -6.0 | -3.67 | 1.62 |
| [13 (low)](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_13/Farbchart_low.jpg) | 7.5 | 106 | -0.7 | -24.0 | -0.42 | 1.76 |
| [15 Pro (low)](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_15_Pro/Farbchart_low.jpg) | 7.8 | 117 | -4.8 | -24.1 | -0.39 | 1.75 |
| [16 (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_16/Foto_iPhone16_ColorChecker_1Lux.jpg) | 7.4 | 116 | -1.5 | -4.5 | -1.03 | 1.77 |

### Inferences
PENDING

### Gaps
PENDING

## Q4. Test conditions from 2015 to 2024 and comparability

### Takeaway
PENDING

### Cited Findings
PENDING

Table 4a. Exposure data stored in the chart files (EXIF). EV100 = log2(N^2/t) - log2(ISO/100).

| Chart | Shot at | Lens | Shutter | Aperture | ISO | BrightnessValue | EV100 | Exposure bias | Edited with |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| [6s](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_6S/ColorChecker_colors_small_iphone6s.jpg) | no EXIF | - | - | - | - | - | - | - | - |
| [7](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_7/Foto_iPhone7_ColorChecker_Compare.jpg) | 2016:09:16 22:07:55 | iPhone 7 back camera 3.99mm f/1.8 | 1/100 s | f/1.8 | 40 | 4.87 | 9.7 | +0.00 | Adobe Photoshop CC 2015.5 (Windows) |
| [8](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_8/Calman.jpg) | 2017:09:22 16:07:06 | iPhone 8 back camera 3.99mm f/1.8 | 1/17 s | f/1.8 | 50 | 2.32 | 6.8 | +0.00 | Adobe Photoshop CS3 Windows |
| [X](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_X/colorchecker_comparison_iphone_x.jpg) | no EXIF | - | - | - | - | - | - | - | - |
| [XS](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_Xs/Foto_iPhoneXS_ColorChecker.jpg) | 2018:09:22 16:12:30 | iPhone XS back dual camera 4.25mm f/1.8 | 1/100 s | f/1.8 | 80 | 3.99 | 8.7 | +0.00 | Adobe Photoshop CC 2018 (Windows) |
| [11](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_11/Foto_iPhone_11_ColoChecker.jpg) | 2019:09:20 21:33:17 | iPhone 11 back dual wide camera 4.25mm f/1.8 | 1/100 s | f/1.8 | 64 | 4.28 | 9.0 | +0.00 | Adobe Photoshop CC 2019 (Windows) |
| [13](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_13/Farbchart_high.jpg) | 2021:10:03 08:39:10 | iPhone 13 back dual wide camera 5.1mm f/1.6 | 1/129 s | f/1.6 | 32 | 5.48 | 10.0 | +0.00 | Adobe Photoshop Elements 11.0 Windows |
| [15 Pro](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_15_Pro/Farbchart_high.jpg) | 2023:10:18 11:45:24 | iPhone 15 Pro back triple camera 2.22mm f/2.2 | 1/50 s | f/2.2 | 500 | 1.04 | 5.6 | +0.00 | Adobe Photoshop Elements 11.0 Windows |
| [16](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_16/Foto_iPhone16_ColorChecker.jpg) | 2024:09:24 13:34:32 | iPhone 16 back dual wide camera 5.96mm f/1.6 | 1/116 s | f/1.6 | 40 | 5.25 | 9.5 | +0.00 | Adobe Photoshop 25.12 (Windows) |
| [11 (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_11/Foto_iPhone_11_ColorChecker_1Lux.jpg) | 2019:09:20 21:27:48 | iPhone 11 back dual wide camera 4.25mm f/1.8 | 1/4 s | f/1.8 | 3200 | -8.14 | -1.3 | +1.95 | Adobe Photoshop CC 2019 (Windows) |
| [13 (low)](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_13/Farbchart_low.jpg) | 2021:10:03 08:44:14 | iPhone 13 back dual wide camera 5.1mm f/1.6 | 1/2 s | f/1.6 | 5000 | -8.76 | -3.3 | +0.13 | Adobe Photoshop Elements 11.0 Windows |
| [15 Pro (low)](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_15_Pro/Farbchart_low.jpg) | 2023:10:18 11:36:10 | iPhone 15 Pro back triple camera 2.22mm f/2.2 | 1/4 s | f/2.2 | 4000 | -6.71 | -1.0 | +0.08 | Adobe Photoshop Elements 11.0 Windows |
| [16 (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_16/Foto_iPhone16_ColorChecker_1Lux.jpg) | 2024:09:24 13:27:25 | iPhone 16 back dual wide camera 5.96mm f/1.6 | 1 s | f/1.6 | 3200 | -8.86 | -3.6 | +0.43 | Adobe Photoshop 25.12 (Windows) |

### Inferences
PENDING

### Gaps
PENDING

## Q5. Summary table

### Takeaway
PENDING

### Cited Findings
Table 5. One row per model, normal light chart. WB = linear R/G % and B/G % on neutral 8 to 3.5. Saturation = chroma ratio as measured, and after WB neutralisation. Hue shifts listed when 4 degrees or more (as measured).

| Model | WB bias on neutrals % | Saturation % (measured / after WB) | Tone | Mean dE00 | Hue shifts (degrees) |
| --- | --- | --- | --- | --- | --- |
| [6s](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_6S/ColorChecker_colors_small_iphone6s.jpg) | R/G +5.2, B/G -11.3 (warm, green) | 112 / 112 | EV -0.22, gamma 1.11, black L* 17 (ref 20), white L* 90 (ref 97) | 4.9 | green -4; red +6 |
| [7](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_7/Foto_iPhone7_ColorChecker_Compare.jpg) | R/G -1.0, B/G -2.0 (mixed, no tint) | 118 / 119 | EV +0.13, gamma 1.15, black L* 14 (ref 20), white L* 95 (ref 97) | 4.6 | light skin -9 |
| [8](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_8/Calman.jpg) | R/G -0.8, B/G -2.8 (mixed, no tint) | 144 / 145 | EV +0.56, gamma 1.66, black L* 12 (ref 20), white L* 100 (ref 97), clipped: 2,7,12,16,19,20 | 9.2 | dark skin -8; light skin -11; purplish blue +5; red +8 |
| [X](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_X/colorchecker_comparison_iphone_x.jpg) | R/G +0.3, B/G -4.2 (mixed, green) | 134 / 134 | EV +0.42, gamma 0.87, black L* 16 (ref 20), white L* 100 (ref 97), clipped: 7,12,16,19 | 7.1 | dark skin -4; light skin -8; foliage -5; red +5 |
| [XS](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_Xs/Foto_iPhoneXS_ColorChecker.jpg) | R/G +6.4, B/G -13.9 (warm, green) | 153 / 155 | EV +0.50, gamma 0.99, black L* 20 (ref 20), white L* 92 (ref 97), clipped: 2,7,9,12,16 | 10.6 | light skin -7; blue +5; red +10 |
| [11](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_11/Foto_iPhone_11_ColoChecker.jpg) | R/G -3.1, B/G -1.1 (mixed, green) | 146 / 147 | EV +0.69, gamma 0.81, black L* 24 (ref 20), white L* 91 (ref 97), clipped: 7,9,16 | 10.9 | light skin -5; green -5; red +8 |
| [13](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_13/Farbchart_high.jpg) | R/G -9.2, B/G -8.7 (mixed, green) | 167 / 173 | EV +0.76, gamma 0.95, black L* 18 (ref 20), white L* 98 (ref 97), clipped: 2,7,8,9,12,16,17 | 12.1 | dark skin +11; purplish blue +6; blue +8; foliage -6; green -7; red +7 |
| [15 Pro](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_15_Pro/Farbchart_high.jpg) | R/G -7.9, B/G -20.0 (mixed, green) | 131 / 141 | EV +0.29, gamma 1.32, black L* 10 (ref 20), white L* 98 (ref 97), clipped: 19 | 9.7 | dark skin +23; light skin +10; blue sky -8; green -5; red +7 |
| [16](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_16/Foto_iPhone16_ColorChecker.jpg) | R/G -1.6, B/G -3.9 (mixed, green) | 150 / 152 | EV +0.86, gamma 0.90, black L* 25 (ref 20), white L* 96 (ref 97), clipped: 7,9,12,16,17 | 10.6 | foliage -9; yellow green -4; green -6; red +5 |
| [X, if file were Display P3](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_X/colorchecker_comparison_iphone_x.jpg) | R/G +0.3, B/G -4.6 (mixed, green) | 159 / 161 | EV +0.42, gamma 0.86, black L* 16 (ref 20), white L* 100 (ref 97), clipped: 2,7,9,12,16,19 | 8.6 | dark skin -7; light skin -11; blue sky -8; red +5 |

### Inferences
PENDING

### Gaps
PENDING

<!-- AUTO-TABLES-END -->

## Handoff

Done so far: review and image URLs for all nine models, downloads, patch grid detection, reference half verification, raw patch sampling (stored in the JSON below).

Patch grid verification already obtained (reference halves versus standard ColorChecker Classic sRGB, 0 to 255 scale): 6s mean abs error 0.29, max 1. 7: 0.29, max 1 (reference pasted as plain sRGB numbers into a Display P3 file). 8: 0.39, max 5.1 after Display P3 to sRGB conversion. X: 0.29, max 1. XS: 0.40, max 5.2 after conversion.

Remaining: derived metrics tables (Delta E, chroma, hue, neutrals, tone), test condition text per review, GSMArena and DPReview cross check.

Temporary files (outside the repository): `/var/folders/2q/td_cw3xx5dqgvp88whr9ydxm0000gn/T/cc_research/` with `pages/` (review HTML), `img/<model>/` (charts), `scripts/` (fetch_pages.py, fetch_images.py, overlay_parse.py, measure.py), `overlays.json`, `out/measurements.json`, `out/debug_sheet.png`.

Re-run sampling:
`/opt/anaconda3/bin/python3 -I "$TMPDIR/cc_research/scripts/measure.py" "$TMPDIR/cc_research/img" "$TMPDIR/cc_research/overlays.json" "$TMPDIR/cc_research/out"`
