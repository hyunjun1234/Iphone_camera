# Measured ColorChecker results for seven Galaxy models (Notebookcheck charts)

Measured on 2026-11-11 from files downloaded from notebookcheck.net, with the scripts and method of the iPhone notes (`colorchecker_measurements.md` in this folder). Normal light chart, main rear camera, automatic settings. Per patch values are in `galaxy_colorchecker_measurements.json` in this folder.

## Q1. Review page and chart image URL per model

### Takeaway
All seven models (S25, S25 Ultra, S24, S24 Ultra, S23, A55 5G, A56 5G) have a Notebookcheck review with a normal light chart and a 1 lux chart. All 28 files (two originals and two inline copies per model) downloaded with HTTP 200. Every chart is a plain phone photo with the reference drawn as an HTML overlay. None has a reference half in the image.

### Cited Findings

| Model | Review page (English page published, author) | Original chart image | 1 lux chart | File facts (read from the file on 2026-11-11) |
| --- | --- | --- | --- | --- |
| S25 | [review](https://www.notebookcheck.net/Samsung-Galaxy-S25-review-The-star-among-compact-smartphones-is-losing-ground.989246.0.html) (2025-03-28, Marcus Herbrich) | [image](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25/Foto_GalaxyS25_ColorChecker.jpg) | [1 lux](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25/Foto_GalaxyS25_ColorChecker_1Lux.jpg) | 1376x917, EXIF "Galaxy S25", 5.4 mm f/1.8, shot 2025-02-19 |
| S25 Ultra | [review](https://www.notebookcheck.net/Samsung-Galaxy-S25-Ultra-review-The-AI-phone-relies-fully-on-artificial-intelligence-and-small-hardware-upgrades.968346.0.html) (2025-02-28, Daniel Schmidt) | [image](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25_Ultra/Foto_GalaxyS25Ultra_ColorChecker.jpg) | [1 lux](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25_Ultra/Foto_GalaxyS25Ultra_ColorChecker_1Lux.jpg) | 1365x910, EXIF "Galaxy S25 Ultra", 6.3 mm f/1.7, shot 2025-02-19 |
| S24 | [review](https://www.notebookcheck.net/Samsung-Galaxy-S24-review-The-best-small-Android-smartphone-gets-cheaper-but-not-better.809474.0.html) (2024-03-05, Marcus Herbrich) | [image](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24/Foto_GalaxyS24_ColorChecker.jpg) | [1 lux](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24/Foto_GalaxyS24_ColorChecker_1Lux.jpg) | 2087x1391, EXIF "Galaxy S24", 5.4 mm f/1.8, shot 2024-02-07 |
| S24 Ultra | [review](https://www.notebookcheck.net/Samsung-Galaxy-S24-Ultra-review-The-ultimate-smartphone-becomes-an-AI-smartphone.809187.0.html) (2024-03-07, Daniel Schmidt) | [image](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24_Ultra/Foto_GalaxyS24Ultra_ColorChecker.jpg) | [1 lux](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24_Ultra/Foto_GalaxyS24Ultra_ColorChecker_1Lux.jpg) | 1586x1058, EXIF "Galaxy S24 Ultra", 6.3 mm f/1.7, shot 2024-01-29 |
| S23 | [review](https://www.notebookcheck.net/Samsung-Galaxy-S23-review-The-small-S23-smartphone-makes-the-biggest-leap.700018.0.html) (2023-03-10, Daniel Schmidt) | [image](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S23/Foto_GalaxyS23_ColorChecker.jpg) | [1 lux](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S23/Foto_GalaxyS23_ColorChecker_1Lux.jpg) | 1999x1333, EXIF "Galaxy S23", 5.4 mm f/1.8, shot 2023-02-08 |
| A55 5G | [review](https://www.notebookcheck.net/Samsung-Galaxy-A55-5G-review-A-lot-of-premium-features-in-a-midrange-smartphone.835803.0.html) (2024-05-11, Daniel Schmidt) | [image](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A55_5G/Foto_GalaxyA55_ColorChecker.jpg) | [1 lux](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A55_5G/Foto_GalaxyA55_ColorChecker_1Lux.jpg) | 2033x1356, EXIF "Galaxy A55 5G", 5.54 mm f/1.8, shot 2024-03-27 |
| A56 5G | [review](https://www.notebookcheck.net/Loads-of-innovations-in-the-premium-mid-range-Samsung-Galaxy-A56-5G-review.1020512.0.html) (2025-05-21, Daniel Schmidt) | [image](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A56_5G/Foto_GalaxyA56_ColorChecker.jpg) | [1 lux](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A56_5G/Foto_GalaxyA56_ColorChecker_1Lux.jpg) | 1951x1301, EXIF "Galaxy A56 5G", 5.54 mm f/1.8, shot 2025-04-11 |

- All 14 original files carry the ICC profile "DCI-P3 D65 Gamut with sRGB Transfer" and EXIF with 23 mm equivalent focal length and digital zoom 1.0, which is the main camera of each model (files linked in the table).
- The review pages embed only resized copies (573 px wide, no ICC profile, same pixel numbers: mean absolute difference to the original 0.03 to 0.05 on a 0 to 255 scale for the normal light charts). Example: [inline S25 copy](https://www.notebookcheck.net/fileadmin/_processed_/3/2/csm_Foto_GalaxyS25_ColorChecker_3467caf751.jpg). Anyone sampling an inline copy must read it as P3, not sRGB.
- The original file is not linked on the page. Its URL is the review's image folder plus the inline file name without the `csm_` prefix and the hash, for example `fileadmin/Notebooks/Samsung/Galaxy_S25/` plus `Foto_GalaxyS25_ColorChecker.jpg` ([S25 review](https://www.notebookcheck.net/Samsung-Galaxy-S25-review-The-star-among-compact-smartphones-is-losing-ground.989246.0.html)).
- Delta E printed in the figure captions, normal light chart (mean, min to max): S25 3.39 (1.05 to 8.05), S25 Ultra 4.55 (0.86 to 9.16), S24 5.04 (0.7 to 11.3), S24 Ultra 4.64 (1.24 to 9.68), S23 9.23 (3.19 to 16.04), A55 5G 4.24 (1.85 to 8.42), A56 5G 4.28 (0.73 to 9.67). 1 lux chart means: 14.12, 7.99, 20.15, 15.57, 16.89, 25.54, 17.72 in the same order ([S25](https://www.notebookcheck.net/Samsung-Galaxy-S25-review-The-star-among-compact-smartphones-is-losing-ground.989246.0.html), [S25 Ultra](https://www.notebookcheck.net/Samsung-Galaxy-S25-Ultra-review-The-AI-phone-relies-fully-on-artificial-intelligence-and-small-hardware-upgrades.968346.0.html), [S24](https://www.notebookcheck.net/Samsung-Galaxy-S24-review-The-best-small-Android-smartphone-gets-cheaper-but-not-better.809474.0.html), [S24 Ultra](https://www.notebookcheck.net/Samsung-Galaxy-S24-Ultra-review-The-ultimate-smartphone-becomes-an-AI-smartphone.809187.0.html), [S23](https://www.notebookcheck.net/Samsung-Galaxy-S23-review-The-small-S23-smartphone-makes-the-biggest-leap.700018.0.html), [A55 5G](https://www.notebookcheck.net/Samsung-Galaxy-A55-5G-review-A-lot-of-premium-features-in-a-midrange-smartphone.835803.0.html), [A56 5G](https://www.notebookcheck.net/Loads-of-innovations-in-the-premium-mid-range-Samsung-Galaxy-A56-5G-review.1020512.0.html)).

### Inferences
- The profile name describes P3 primaries, D65 white and the sRGB tone curve, which is the definition of Display P3. The script treats it as Display P3. A LittleCMS conversion with the embedded profile differs from the script's matrix conversion by at most 0.77 to 2.33 (0 to 255 scale, in gamut patches, Table 2c).
- The charts were shot 1 to 6 weeks before the English page date, so the English pages are probably later translations of German originals (unverified, German pages not opened).

### Gaps
- No Galaxy model other than these seven was searched.
- The German pages on notebookcheck.com were not opened.

<!-- AUTO-TABLES-BEGIN -->
## Q2. Average sRGB values of the phone half and the reference half, all 24 patches

### Takeaway
All seven charts are plain phone photos, so the pixels contain no reference half. The phone colour was sampled in the middle of each patch (Table 2a). The reference was read from the page overlay and matches the standard ColorChecker Classic sRGB values with a mean absolute error of 0.01 and a maximum of 1 (white patch, blue channel 243 instead of 242) in all 14 overlays (Tables 2b, 2c).

### Cited Findings
Table 2a. Phone colour per patch as sRGB R,G,B (0 to 255). Mean of the centre window (middle 50 % of the width and height) of each patch in the plain phone photo. All seven files carry the ICC profile "DCI-P3 D65 Gamut with sRGB Transfer" and were converted to sRGB (D65, matrix conversion); a trailing * marks a colour outside the sRGB gamut, shown clipped.

| # | Patch | Reference | S25 | S25 Ultra | S24 | S24 Ultra | S23 | A55 5G | A56 5G |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | dark skin | 115,82,68 | 114,78,61 | 110,74,59 | 150,103,87 | 106,68,57 | 154,108,87 | 126,79,68 | 139,91,75 |
| 2 | light skin | 194,150,130 | 205,146,132 | 198,139,129 | 219,148,136 | 195,135,124 | 229,180,165 | 201,142,130 | 206,147,131 |
| 3 | blue sky | 98,122,157 | 75,112,154 | 75,108,152 | 88,129,178 | 68,104,149 | 103,156,218 | 84,126,168 | 91,131,170 |
| 4 | foliage | 87,108,67 | 84,105,53 | 81,101,51 | 117,134,80 | 82,103,51 | 109,150,71 | 106,126,70 | 111,131,69 |
| 5 | blue flower | 133,128,177 | 125,120,175 | 121,112,171 | 142,132,197 | 123,113,177 | 165,167,224 | 148,133,183 | 146,135,182 |
| 6 | bluish green | 103,189,170 | 94,186,174 | 95,182,171 | 109,197,187 | 96,189,176 | 124,221,208 | 116,200,195 | 118,202,193 |
| 7 | orange | 214,126,44 | 225,128,30 | 217,118,20 | 238,125,26 | 220,112,29 | 254,152,55 | 222,113,0* | 227,119,0* |
| 8 | purplish blue | 80,91,166 | 64,84,178 | 60,78,172 | 65,89,196 | 52,68,163 | 82,117,239 | 70,92,179 | 77,100,182 |
| 9 | moderate red | 193,90,99 | 210,74,86 | 204,65,84 | 230,76,89 | 211,57,80 | 255,102,110 | 211,64,80 | 210,70,79 |
| 10 | purple | 94,60,108 | 78,43,89 | 71,38,85 | 109,58,121 | 74,36,84 | 114,71,143 | 108,61,102 | 115,69,108 |
| 11 | yellow green | 157,188,64 | 150,193,10 | 149,189,16 | 175,207,47 | 155,196,17 | 177,220,20 | 178,209,44 | 175,205,30 |
| 12 | orange yellow | 224,163,46 | 225,158,0* | 224,155,0* | 238,162,0* | 237,158,0* | 250,197,2 | 245,159,0* | 243,157,0* |
| 13 | blue | 56,61,150 | 29,45,152 | 27,38,146 | 21,48,184 | 17,28,138 | 51,71,205 | 24,53,163 | 35,65,170 |
| 14 | green | 70,148,73 | 53,143,51 | 57,139,50 | 81,177,80 | 53,141,49 | 74,183,63 | 61,170,78 | 70,175,81 |
| 15 | red | 175,54,60 | 191,40,57 | 177,29,51 | 219,36,61 | 187,18,50 | 236,65,64 | 202,25,43 | 202,33,42 |
| 16 | yellow | 231,199,31 | 233,195,0* | 231,192,0* | 238,195,0* | 239,194,0* | 241,216,0* | 251,195,0* | 245,191,0* |
| 17 | magenta | 187,86,149 | 193,67,141 | 187,56,141 | 213,62,150 | 194,50,142 | 233,100,191 | 210,77,140 | 205,76,135 |
| 18 | cyan | 8,133,161 | 0,123,165* | 0,119,163* | 0,137,195* | 0,123,171* | 0,168,225* | 0,153,195* | 0,153,192* |
| 19 | white 9.5 | 243,243,242 | 230,234,224 | 223,225,218 | 220,217,213 | 219,218,212 | 233,232,227 | 226,219,219 | 229,221,215 |
| 20 | neutral 8 | 200,200,200 | 193,198,197 | 187,192,193 | 197,199,200 | 191,194,192 | 211,213,210 | 197,195,198 | 197,195,195 |
| 21 | neutral 6.5 | 160,160,160 | 154,159,160 | 151,152,155 | 173,172,178 | 156,157,159 | 184,186,185 | 170,168,171 | 168,166,167 |
| 22 | neutral 5 | 122,122,121 | 112,114,113 | 106,108,110 | 137,135,138 | 115,115,115 | 140,140,140 | 134,128,133 | 132,127,128 |
| 23 | neutral 3.5 | 85,85,85 | 71,73,75 | 68,69,73 | 95,96,102 | 72,73,76 | 90,94,95 | 92,90,95 | 96,94,97 |
| 24 | black 2 | 52,52,52 | 28,29,31 | 27,28,30 | 50,48,54 | 31,32,36 | 44,43,48 | 49,48,54 | 61,61,64 |

Sources: [S25](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25/Foto_GalaxyS25_ColorChecker.jpg), [S25 Ultra](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25_Ultra/Foto_GalaxyS25Ultra_ColorChecker.jpg), [S24](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24/Foto_GalaxyS24_ColorChecker.jpg), [S24 Ultra](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24_Ultra/Foto_GalaxyS24Ultra_ColorChecker.jpg), [S23](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S23/Foto_GalaxyS23_ColorChecker.jpg), [A55 5G](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A55_5G/Foto_GalaxyA55_ColorChecker.jpg), [A56 5G](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A56_5G/Foto_GalaxyA56_ColorChecker.jpg).

Table 2b. Reference colour per patch. These charts have no reference burnt into the image. The review page draws it as an HTML box (`colorchk_box` with `background-color:rgb(...)`) over the middle of each patch, so the reference is read from the page source as exact sRGB numbers, not sampled from pixels.

| # | Patch | Standard | Overlay reference (identical in all 14 overlays) | Difference |
| --- | --- | --- | --- | --- |
| 1 | dark skin | 115,82,68 | 115,82,68 | +0,+0,+0 |
| 2 | light skin | 194,150,130 | 194,150,130 | +0,+0,+0 |
| 3 | blue sky | 98,122,157 | 98,122,157 | +0,+0,+0 |
| 4 | foliage | 87,108,67 | 87,108,67 | +0,+0,+0 |
| 5 | blue flower | 133,128,177 | 133,128,177 | +0,+0,+0 |
| 6 | bluish green | 103,189,170 | 103,189,170 | +0,+0,+0 |
| 7 | orange | 214,126,44 | 214,126,44 | +0,+0,+0 |
| 8 | purplish blue | 80,91,166 | 80,91,166 | +0,+0,+0 |
| 9 | moderate red | 193,90,99 | 193,90,99 | +0,+0,+0 |
| 10 | purple | 94,60,108 | 94,60,108 | +0,+0,+0 |
| 11 | yellow green | 157,188,64 | 157,188,64 | +0,+0,+0 |
| 12 | orange yellow | 224,163,46 | 224,163,46 | +0,+0,+0 |
| 13 | blue | 56,61,150 | 56,61,150 | +0,+0,+0 |
| 14 | green | 70,148,73 | 70,148,73 | +0,+0,+0 |
| 15 | red | 175,54,60 | 175,54,60 | +0,+0,+0 |
| 16 | yellow | 231,199,31 | 231,199,31 | +0,+0,+0 |
| 17 | magenta | 187,86,149 | 187,86,149 | +0,+0,+0 |
| 18 | cyan | 8,133,161 | 8,133,161 | +0,+0,+0 |
| 19 | white 9.5 | 243,243,242 | 243,243,243 | +0,+0,+1 |
| 20 | neutral 8 | 200,200,200 | 200,200,200 | +0,+0,+0 |
| 21 | neutral 6.5 | 160,160,160 | 160,160,160 | +0,+0,+0 |
| 22 | neutral 5 | 122,122,121 | 122,122,121 | +0,+0,+0 |
| 23 | neutral 3.5 | 85,85,85 | 85,85,85 | +0,+0,+0 |
| 24 | black 2 | 52,52,52 | 52,52,52 | +0,+0,+0 |

Table 2c. Fit of the overlay reference to the standard ColorChecker Classic sRGB values, and checks of the patch grid. Grid offset = largest distance between the centre of a detected patch and the centre of the site's overlay box, in percent of the image size. Sample difference = largest difference (0 to 255) between sampling inside the detected patch and inside the site's box. Noise = largest standard deviation inside a sampling window. ICC check = largest difference between the matrix conversion used here and a LittleCMS conversion with the embedded profile, in gamut patches only. Inline copy = mean absolute difference between the stored numbers of the original file and of the resized copy shown on the page.

| Chart | Reference mean abs error (0 to 255) | Reference max abs error | Reference mean dE76 | Grid offset % | Sample difference | Noise | ICC check | Inline copy |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| S25 | 0.01 | 1.0 | 0.02 | 1.54 | 0.78 | 2.5 | 0.95 | 0.04 |
| S25 Ultra | 0.01 | 1.0 | 0.02 | 1.09 | 0.55 | 2.9 | 0.77 | 0.04 |
| S24 | 0.01 | 1.0 | 0.02 | 1.58 | 0.70 | 3.8 | 0.97 | 0.05 |
| S24 Ultra | 0.01 | 1.0 | 0.02 | 0.97 | 0.55 | 3.6 | 1.28 | 0.04 |
| S23 | 0.01 | 1.0 | 0.02 | 1.09 | 0.76 | 4.7 | 2.33 | 0.03 |
| A55 5G | 0.01 | 1.0 | 0.02 | 0.91 | 0.37 | 4.3 | 0.96 | 0.03 |
| A56 5G | 0.01 | 1.0 | 0.02 | 0.80 | 0.35 | 4.0 | 0.97 | 0.04 |
| S25 (1 lux) | 0.01 | 1.0 | 0.02 | 1.49 | 1.37 | 3.3 | 1.10 | 0.06 |
| S25 Ultra (1 lux) | 0.01 | 1.0 | 0.02 | 1.15 | 1.87 | 4.7 | 0.95 | 0.05 |
| S24 (1 lux) | 0.01 | 1.0 | 0.02 | 1.58 | 1.01 | 3.9 | 0.98 | 0.07 |
| S24 Ultra (1 lux) | 0.01 | 1.0 | 0.02 | 1.75 | 3.77 | 6.0 | 1.02 | 0.06 |
| S23 (1 lux) | 0.01 | 1.0 | 0.02 | 0.95 | 1.38 | 5.1 | 0.99 | 0.06 |
| A55 5G (1 lux) | 0.01 | 1.0 | 0.02 | 2.08 | 2.27 | 5.5 | 0.98 | 0.11 |
| A56 5G (1 lux) | 0.01 | 1.0 | 0.02 | 1.34 | 1.78 | 4.2 | 1.15 | 0.09 |

Sources: [S25](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25/Foto_GalaxyS25_ColorChecker.jpg), [S25 Ultra](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25_Ultra/Foto_GalaxyS25Ultra_ColorChecker.jpg), [S24](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24/Foto_GalaxyS24_ColorChecker.jpg), [S24 Ultra](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24_Ultra/Foto_GalaxyS24Ultra_ColorChecker.jpg), [S23](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S23/Foto_GalaxyS23_ColorChecker.jpg), [A55 5G](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A55_5G/Foto_GalaxyA55_ColorChecker.jpg), [A56 5G](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A56_5G/Foto_GalaxyA56_ColorChecker.jpg), [S25 (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25/Foto_GalaxyS25_ColorChecker_1Lux.jpg), [S25 Ultra (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25_Ultra/Foto_GalaxyS25Ultra_ColorChecker_1Lux.jpg), [S24 (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24/Foto_GalaxyS24_ColorChecker_1Lux.jpg), [S24 Ultra (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24_Ultra/Foto_GalaxyS24Ultra_ColorChecker_1Lux.jpg), [S23 (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S23/Foto_GalaxyS23_ColorChecker_1Lux.jpg), [A55 5G (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A55_5G/Foto_GalaxyA55_ColorChecker_1Lux.jpg), [A56 5G (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A56_5G/Foto_GalaxyA56_ColorChecker_1Lux.jpg).

Table 2d. Delta E printed on the review page for each chart against a recomputation from the patches sampled here. "As sRGB" reads the stored numbers as sRGB and ignores the P3 profile, "profile applied" converts with the profile first. Both use CIEDE2000 against the standard reference. The last column is the value used in Q3 and Q5 (profile applied, sampled in the detected patch).

| Chart | Site mean of 24 | Site min to max | Recomputed, as sRGB | Mean abs diff per patch | Correlation | Recomputed, profile applied (site box) | Mean dE00 used in Q3 |
| --- | --- | --- | --- | --- | --- | --- | --- |
| [S25](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25/Foto_GalaxyS25_ColorChecker.jpg) | 3.39 | 1.00 to 8.10 | 3.40 | 0.32 | 0.976 | 4.00 | 4.00 |
| [S25 Ultra](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25_Ultra/Foto_GalaxyS25Ultra_ColorChecker.jpg) | 4.56 | 0.90 to 9.20 | 4.61 | 0.30 | 0.985 | 4.92 | 4.91 |
| [S24](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24/Foto_GalaxyS24_ColorChecker.jpg) | 5.04 | 0.70 to 11.30 | 4.84 | 0.31 | 0.992 | 5.86 | 5.89 |
| [S24 Ultra](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24_Ultra/Foto_GalaxyS24Ultra_ColorChecker.jpg) | 4.63 | 1.20 to 9.70 | 4.74 | 0.34 | 0.988 | 5.24 | 5.23 |
| [S23](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S23/Foto_GalaxyS23_ColorChecker.jpg) | 9.23 | 3.20 to 16.00 | 8.87 | 0.46 | 0.994 | 9.44 | 9.50 |
| [A55 5G](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A55_5G/Foto_GalaxyA55_ColorChecker.jpg) | 4.23 | 1.80 to 8.40 | 4.21 | 0.35 | 0.981 | 5.37 | 5.37 |
| [A56 5G](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A56_5G/Foto_GalaxyA56_ColorChecker.jpg) | 4.28 | 0.70 to 9.70 | 4.21 | 0.30 | 0.989 | 5.34 | 5.36 |
| [S25 (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25/Foto_GalaxyS25_ColorChecker_1Lux.jpg) | 14.12 | 5.80 to 20.80 | 14.04 | 0.28 | 0.995 | 14.13 | 14.06 |
| [S25 Ultra (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25_Ultra/Foto_GalaxyS25Ultra_ColorChecker_1Lux.jpg) | 8.00 | 2.00 to 15.50 | 8.22 | 0.33 | 0.995 | 9.03 | 8.89 |
| [S24 (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24/Foto_GalaxyS24_ColorChecker_1Lux.jpg) | 20.15 | 9.60 to 32.30 | 20.08 | 0.29 | 0.998 | 20.01 | 19.90 |
| [S24 Ultra (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24_Ultra/Foto_GalaxyS24Ultra_ColorChecker_1Lux.jpg) | 15.57 | 9.20 to 24.60 | 15.47 | 0.30 | 0.995 | 15.37 | 15.17 |
| [S23 (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S23/Foto_GalaxyS23_ColorChecker_1Lux.jpg) | 16.90 | 8.50 to 26.10 | 16.88 | 0.32 | 0.996 | 17.07 | 16.96 |
| [A55 5G (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A55_5G/Foto_GalaxyA55_ColorChecker_1Lux.jpg) | 25.55 | 13.50 to 35.00 | 25.18 | 0.38 | 0.998 | 25.07 | 24.92 |
| [A56 5G (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A56_5G/Foto_GalaxyA56_ColorChecker_1Lux.jpg) | 17.72 | 9.90 to 26.20 | 17.38 | 0.44 | 0.995 | 17.36 | 17.27 |

### Inferences
- The patch grid is right in all 14 files. Detected patch centres sit within 0.8 to 2.1 % of the image size of the site's own overlay boxes, and the Delta E recomputed per patch correlates 0.976 to 0.998 with the numbers printed on the page (Tables 2c, 2d).
- Notebookcheck's Delta E for these charts is CIEDE2000 on the stored pixel numbers read as sRGB, with the embedded P3 profile ignored. The iPhone notes found the same. Recomputed that way, the normal light means are within 0.36 of the page (largest gap S23: 8.87 against 9.23). With the profile applied they are 0.3 to 1.1 higher than the page (Table 2d).
- The overlays of the iPhone 11, 13, 15 Pro and 16 reviews carry exactly the same 24 reference numbers as the Galaxy overlays (compared in the saved page data of both runs). The 2015 to 2018 composites were within 1 of the standard in 6s, 7 and X, per the iPhone notes. The reference therefore adds no difference between years.

### Gaps
- The 1 lux charts are a supplement (Tables 2c, 2d, 3g, 4a). Their per patch values are in the JSON file only, not in Table 2a.
- The site's sampling area is not published. The per patch gap of 0.28 to 0.46 Delta E between the page and the recomputation is unexplained (a different sampling area is likely, unverified).

## Q3. Delta E, saturation, hue shifts, white balance and tone per model

### Takeaway
In normal light the seven Galaxy charts show a mean Delta E 2000 of 4.0 to 5.9 (S23: 9.5), chroma at 117 to 136 % of the reference, mid grey within -0.39 to +0.43 EV of the reference, and neutral patches within 7.4 % of grey in linear R/G and B/G. The largest hue shift is on light skin: -7 to -17 degrees in every model, still -7 to -15 after white balance neutralisation.

### Cited Findings
All values below are computed from Table 2a against the standard reference (CIELAB, D65 white). Chroma is C*ab, shown as phone divided by reference in percent (100 = same saturation). "After WB" means the phone values were first multiplied by linear gains that make neutral 8 to neutral 3.5 grey, which removes the colour cast before saturation and hue are compared.

Table 3a. Colour error and chroma ratio, as measured.

| Model | Mean dE76 | Mean dE00 | Max dE00 | Chroma % (mean of 18) | Chroma % (fit slope) | skin 1,2 | reds 9,15 | yellow/orange 7,12,16 | greens 4,11,14 | blues 3,8,13 | red 15 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| S25 | 8.3 | 4.0 | 7.6 (black 2) | 117 | 119 | 117 | 125 | 117 | 122 | 123 | 119 |
| S25 Ultra | 9.4 | 4.9 | 7.9 (black 2) | 117 | 119 | 114 | 124 | 117 | 118 | 127 | 115 |
| S24 | 12.9 | 5.9 | 11.0 (foliage) | 131 | 130 | 137 | 142 | 123 | 121 | 144 | 138 |
| S24 Ultra | 10.6 | 5.2 | 9.2 (blue) | 122 | 123 | 117 | 136 | 117 | 122 | 131 | 126 |
| S23 | 16.8 | 9.5 | 16.5 (foliage) | 136 | 134 | 117 | 141 | 124 | 153 | 157 | 138 |
| A55 5G | 11.4 | 5.4 | 7.8 (foliage) | 123 | 127 | 126 | 139 | 136 | 124 | 122 | 137 |
| A56 5G | 11.2 | 5.4 | 9.9 (foliage) | 122 | 126 | 130 | 136 | 137 | 129 | 114 | 135 |

Sources: [S25](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25/Foto_GalaxyS25_ColorChecker.jpg), [S25 Ultra](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25_Ultra/Foto_GalaxyS25Ultra_ColorChecker.jpg), [S24](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24/Foto_GalaxyS24_ColorChecker.jpg), [S24 Ultra](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24_Ultra/Foto_GalaxyS24Ultra_ColorChecker.jpg), [S23](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S23/Foto_GalaxyS23_ColorChecker.jpg), [A55 5G](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A55_5G/Foto_GalaxyA55_ColorChecker.jpg), [A56 5G](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A56_5G/Foto_GalaxyA56_ColorChecker.jpg).

Table 3b. Chroma ratio after WB neutralisation. "C*/(L*+16)" also divides by lightness, so it is insensitive to exposure.

| Model | Chroma % (mean of 18) | C*/(L*+16) % | skin 1,2 | reds 9,15 | yellow/orange 7,12,16 | greens 4,11,14 | blues 3,8,13 | red 15 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| S25 | 119 | 122 | 126 | 129 | 119 | 121 | 122 | 122 |
| S25 Ultra | 117 | 125 | 122 | 127 | 118 | 119 | 121 | 118 |
| S24 | 129 | 123 | 141 | 143 | 122 | 124 | 136 | 139 |
| S24 Ultra | 123 | 132 | 121 | 137 | 117 | 123 | 128 | 127 |
| S23 | 137 | 117 | 123 | 144 | 125 | 152 | 158 | 140 |
| A55 5G | 120 | 117 | 122 | 136 | 132 | 128 | 114 | 134 |
| A56 5G | 120 | 115 | 124 | 133 | 135 | 130 | 111 | 132 |

Sources: [S25](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25/Foto_GalaxyS25_ColorChecker.jpg), [S25 Ultra](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25_Ultra/Foto_GalaxyS25Ultra_ColorChecker.jpg), [S24](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24/Foto_GalaxyS24_ColorChecker.jpg), [S24 Ultra](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24_Ultra/Foto_GalaxyS24Ultra_ColorChecker.jpg), [S23](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S23/Foto_GalaxyS23_ColorChecker.jpg), [A55 5G](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A55_5G/Foto_GalaxyA55_ColorChecker.jpg), [A56 5G](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A56_5G/Foto_GalaxyA56_ColorChecker.jpg).

Table 3c. Hue shift in degrees of CIELAB hue angle, phone minus reference, as measured / after WB neutralisation. Positive means: skin and red toward yellow, greens toward cyan, blues toward purple.

| Model | 1 dark skin | 2 light skin | 3 blue sky | 8 purplish blue | 13 blue | 4 foliage | 11 yellow green | 14 green | 15 red |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| S25 | +2.1 / +0.6 | -12.4 / -13.1 | -3.0 / -2.3 | +0.7 / +1.0 | +1.7 / +1.7 | -4.7 / -6.5 | -0.1 / -1.2 | -2.7 / -3.0 | -0.5 / +0.1 |
| S25 Ultra | -1.0 / -0.1 | -16.8 / -14.2 | +1.1 / -0.4 | +1.8 / +1.5 | +3.1 / +3.0 | -4.9 / -6.5 | -0.4 / -1.2 | -3.5 / -4.1 | -1.4 / -0.4 |
| S24 | -5.0 / -2.0 | -16.9 / -13.2 | -0.8 / -4.0 | +1.4 / +0.7 | +2.7 / +2.3 | -7.7 / -8.3 | -2.4 / -2.5 | -1.6 / -2.2 | +0.1 / +0.9 |
| S24 Ultra | -7.5 / -6.9 | -16.3 / -15.2 | +0.6 / -0.0 | +3.5 / +3.4 | +4.4 / +4.4 | -4.7 / -5.6 | -0.4 / -0.8 | -3.2 / -3.4 | -1.2 / -0.7 |
| S23 | +0.7 / -0.7 | -7.2 / -9.1 | -0.8 / +0.1 | -0.8 / -0.5 | +0.5 / +0.7 | -0.5 / -1.3 | -1.2 / -2.0 | -4.0 / -4.2 | +5.3 / +5.4 |
| A55 5G | -10.7 / -6.7 | -15.0 / -9.1 | -6.3 / -11.8 | -1.4 / -2.8 | -0.1 / -0.8 | -6.1 / -5.0 | -3.2 / -2.1 | +1.8 / +1.3 | +4.1 / +4.3 |
| A56 5G | -5.2 / -2.8 | -10.2 / -7.1 | -7.0 / -9.9 | -3.2 / -4.2 | -2.6 / -3.0 | -7.6 / -6.3 | -3.9 / -2.8 | +0.7 / +0.7 | +5.3 / +5.3 |

Sources: [S25](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25/Foto_GalaxyS25_ColorChecker.jpg), [S25 Ultra](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25_Ultra/Foto_GalaxyS25Ultra_ColorChecker.jpg), [S24](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24/Foto_GalaxyS24_ColorChecker.jpg), [S24 Ultra](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24_Ultra/Foto_GalaxyS24Ultra_ColorChecker.jpg), [S23](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S23/Foto_GalaxyS23_ColorChecker.jpg), [A55 5G](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A55_5G/Foto_GalaxyA55_ColorChecker.jpg), [A56 5G](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A56_5G/Foto_GalaxyA56_ColorChecker.jpg).

Table 3d. White balance on the neutral patches (mean of neutral 8, 6.5, 5, 3.5; clipped patches left out). Ratios in percent deviation from 1. Linear = linear light sRGB, encoded = gamma encoded sRGB numbers. Tint = G against the mean of R and B (linear). CCT and Duv describe the rendered grey on a D65 display (6504 K and 0 would be neutral, lower CCT is warmer, positive Duv is greener).

| Model | R/G % linear | B/G % linear | R/G % encoded | B/G % encoded | Tint % | a* | b* | CCT K | Duv | Label | Patches used |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| S25 | -5.5 | +0.8 | -2.6 | +0.4 | +2.4 | -1.3 | -0.5 | 6755 | +0.0050 | mixed, green | 20,21,22,23 |
| S25 Ultra | -3.5 | +5.1 | -1.7 | +2.4 | -0.7 | -0.3 | -1.6 | 6880 | +0.0034 | cool, no tint | 20,21,22,23 |
| S24 | +0.0 | +6.7 | +0.0 | +3.0 | -3.2 | +0.8 | -2.1 | 6808 | +0.0018 | mixed, magenta | 20,21,22,23 |
| S24 Ultra | -2.0 | +2.4 | -1.0 | +1.1 | -0.1 | -0.3 | -0.7 | 6692 | +0.0034 | cool, no tint | 20,21,22,23 |
| S23 | -3.7 | -0.5 | -1.7 | -0.2 | +2.1 | -1.0 | +0.1 | 6629 | +0.0046 | mixed, green | 20,21,22,23 |
| A55 5G | +5.5 | +7.4 | +2.5 | +3.4 | -6.0 | +2.1 | -1.9 | 6625 | -0.0002 | mixed, magenta | 20,21,22,23 |
| A56 5G | +4.8 | +2.8 | +2.2 | +1.3 | -3.6 | +1.4 | -0.5 | 6448 | +0.0010 | mixed, magenta | 20,21,22,23 |

Sources: [S25](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25/Foto_GalaxyS25_ColorChecker.jpg), [S25 Ultra](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25_Ultra/Foto_GalaxyS25Ultra_ColorChecker.jpg), [S24](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24/Foto_GalaxyS24_ColorChecker.jpg), [S24 Ultra](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24_Ultra/Foto_GalaxyS24Ultra_ColorChecker.jpg), [S23](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S23/Foto_GalaxyS23_ColorChecker.jpg), [A55 5G](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A55_5G/Foto_GalaxyA55_ColorChecker.jpg), [A56 5G](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A56_5G/Foto_GalaxyA56_ColorChecker.jpg).

Table 3e. Encoded R/G % and B/G % for each neutral patch (R/G, B/G).

| Model | 19 white 9.5 | 20 neutral 8 | 21 neutral 6.5 | 22 neutral 5 | 23 neutral 3.5 | 24 black 2 |
| --- | --- | --- | --- | --- | --- | --- |
| S25 | -1.6, -4.3 | -2.6, -0.6 | -3.2, +0.4 | -2.2, -1.0 | -2.3, +2.8 | -4.2, +7.5 |
| S25 Ultra | -0.6, -2.9 | -2.6, +0.5 | -1.0, +1.4 | -1.2, +2.0 | -1.8, +5.8 | -4.6, +8.2 |
| S24 | +1.3, -1.9 | -0.8, +0.7 | +0.5, +3.1 | +1.4, +2.6 | -1.1, +5.7 | +3.3, +12.3 |
| S24 Ultra | +0.6, -2.5 | -1.3, -0.6 | -0.8, +1.3 | -0.0, +0.0 | -1.7, +3.8 | -2.7, +12.4 |
| S23 | +0.5, -2.4 | -1.2, -1.6 | -1.4, -0.6 | -0.0, +0.0 | -4.4, +1.4 | +2.9, +12.8 |
| A55 5G | +3.5, +0.1 | +1.3, +1.9 | +1.5, +2.0 | +4.9, +3.5 | +2.2, +6.1 | +2.6, +11.4 |
| A56 5G | +3.7, -2.8 | +1.2, +0.4 | +1.5, +0.7 | +4.0, +0.9 | +2.0, +3.2 | -0.0, +3.6 |

Sources: [S25](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25/Foto_GalaxyS25_ColorChecker.jpg), [S25 Ultra](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25_Ultra/Foto_GalaxyS25Ultra_ColorChecker.jpg), [S24](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24/Foto_GalaxyS24_ColorChecker.jpg), [S24 Ultra](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24_Ultra/Foto_GalaxyS24Ultra_ColorChecker.jpg), [S23](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S23/Foto_GalaxyS23_ColorChecker.jpg), [A55 5G](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A55_5G/Foto_GalaxyA55_ColorChecker.jpg), [A56 5G](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A56_5G/Foto_GalaxyA56_ColorChecker.jpg).

Table 3f. Tone on the neutral patches. L* phone per patch (reference L* in the header). EV at N5 = log2 of phone luminance over reference luminance at neutral 5. Gamma = slope of log luminance, phone against reference, over the unclipped patches of neutral 8 to 3.5 (above 1 is more contrast). Residual = phone L* minus the L* the reference would have after the same EV shift (positive in the shadows is lift, negative in the highlights is compression).

| Model | white 9.5 (95.8) | neutral 8 (80.6) | neutral 6.5 (65.9) | neutral 5 (51.2) | neutral 3.5 (36.1) | black 2 (21.7) | EV at N5 | Gamma | L* slope N8 to black | Residual white | Residual N8 | Residual N3.5 | Residual black | Clipped patches |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| S25 | 92.0 | 79.6 | 65.3 | 47.9 | 31.0 | 10.6 | -0.21 | 1.16 | 1.17 | +1.6 | +3.7 | -2.6 | -9.3 | none |
| S25 Ultra | 89.2 | 77.5 | 63.0 | 45.4 | 29.4 | 10.1 | -0.39 | 1.18 | 1.14 | +3.0 | +5.2 | -2.2 | -8.3 | none |
| S24 | 86.8 | 80.0 | 70.7 | 56.5 | 41.0 | 20.3 | +0.33 | 0.86 | 1.01 | -13.2 | -8.2 | +0.7 | -4.3 | none |
| S24 Ultra | 86.9 | 78.1 | 64.7 | 48.5 | 31.1 | 12.4 | -0.18 | 1.13 | 1.12 | -4.4 | +1.4 | -3.0 | -7.8 | none |
| S23 | 92.0 | 85.0 | 75.4 | 58.2 | 39.5 | 17.7 | +0.43 | 0.99 | 1.16 | -8.0 | -5.7 | -2.1 | -8.0 | 7,9 |
| A55 5G | 87.9 | 79.0 | 69.1 | 54.3 | 38.5 | 20.2 | +0.20 | 0.91 | 1.00 | -12.1 | -6.1 | -0.1 | -3.3 | 16 |
| A56 5G | 88.6 | 78.8 | 68.2 | 53.6 | 40.1 | 26.0 | +0.15 | 0.86 | 0.91 | -11.1 | -5.2 | +2.1 | +2.9 | none |

Sources: [S25](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25/Foto_GalaxyS25_ColorChecker.jpg), [S25 Ultra](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25_Ultra/Foto_GalaxyS25Ultra_ColorChecker.jpg), [S24](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24/Foto_GalaxyS24_ColorChecker.jpg), [S24 Ultra](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24_Ultra/Foto_GalaxyS24Ultra_ColorChecker.jpg), [S23](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S23/Foto_GalaxyS23_ColorChecker.jpg), [A55 5G](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A55_5G/Foto_GalaxyA55_ColorChecker.jpg), [A56 5G](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A56_5G/Foto_GalaxyA56_ColorChecker.jpg).

Table 3g. Low light charts (supplementary, same method).

| Chart | Mean dE00 | Chroma % (mean of 18) | R/G % linear | B/G % linear | EV at N5 | Gamma |
| --- | --- | --- | --- | --- | --- | --- |
| [S25 (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25/Foto_GalaxyS25_ColorChecker_1Lux.jpg) | 14.1 | 118 | +15.8 | -1.8 | -1.65 | 1.62 |
| [S25 Ultra (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25_Ultra/Foto_GalaxyS25Ultra_ColorChecker_1Lux.jpg) | 8.9 | 130 | +3.2 | -1.7 | -1.28 | 1.85 |
| [S24 (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24/Foto_GalaxyS24_ColorChecker_1Lux.jpg) | 19.9 | 113 | +17.9 | +72.6 | -1.97 | 1.77 |
| [S24 Ultra (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24_Ultra/Foto_GalaxyS24Ultra_ColorChecker_1Lux.jpg) | 15.2 | 101 | +7.5 | -16.1 | -1.92 | 1.71 |
| [S23 (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S23/Foto_GalaxyS23_ColorChecker_1Lux.jpg) | 17.0 | 116 | +37.2 | +0.7 | -2.31 | 2.05 |
| [A55 5G (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A55_5G/Foto_GalaxyA55_ColorChecker_1Lux.jpg) | 24.9 | 87 | -9.0 | -32.4 | -2.91 | 1.72 |
| [A56 5G (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A56_5G/Foto_GalaxyA56_ColorChecker_1Lux.jpg) | 17.3 | 102 | +3.7 | -24.6 | -1.97 | 1.62 |

### Inferences
- Two tone renderings appear. S25, S25 Ultra and S24 Ultra: EV -0.18 to -0.39, gamma 1.13 to 1.18, black patch L* 10 to 12 against a reference of 22 (darker mid grey, deeper shadows). S24, A55 and A56: EV +0.15 to +0.33, gamma 0.86 to 0.91, black L* 20 to 26, white L* 87 to 89 against 96 (brighter mid grey, flatter curve, highlights held down). S23 has EV +0.43 and gamma 0.99. Clipped channels occur only on S23 (patches 7 and 9) and A55 (patch 16).
- White balance: the five S models stay within R/G -5.5 to 0.0 and B/G -0.5 to +6.7 (neutral to slightly cool, rendered grey 6629 to 6880 K). A55 and A56 have R/G +4.8 to +5.5 with B/G +2.8 to +7.4, a magenta tint (Tint -3.6 and -6.0 %).
- The chroma ratio falls with each S generation on this chart (S23 136 %, S24 131 %, S25 117 %; S24 Ultra 122 %, S25 Ultra 117 %), but the exposure insensitive measure C*/(L*+16) does not (S23 117, S24 123, S25 122; S24 Ultra 132, S25 Ultra 125, Table 3b). The fall follows the darker exposure of the newer charts and is not evidence of a lower saturation setting. One chart per model.
- White balance neutralisation changes the mean chroma ratio by 3 points or less, so the saturation numbers are not caused by the colour cast.
- Hue: besides light skin, foliage shifts -5 to -8 degrees (toward yellow) in six models (all but S23) and dark skin -5 to -11 in four. Purplish blue and blue stay within 4.4 degrees. Blue sky shifts -6 and -7 on A55 and A56. Red shifts +4 to +5 on S23, A55 and A56.
- At 1 lux mid grey falls 1.3 to 2.9 EV below the reference and gamma rises to 1.6 to 2.1. Mean Delta E 2000 is lowest for S25 Ultra (8.9) and highest for A55 (24.9). The S24 1 lux chart has a strong blue cast (linear B/G +72.6 %).

### Gaps
- One chart and one exposure per model. Shot to shot variation is unknown.
- The spectrum and colour temperature of the lab light are not published, so the white balance bias mixes the camera's auto white balance with the light.
- Firmware versions at the time of each chart were not collected.
- S23 Ultra, S23+, S24+, S25+, S25 Edge and the FE models were not measured (not requested).

## Q4. How Notebookcheck shoots the chart, changes from 2015 to 2025, comparability

### Takeaway
Notebookcheck states that the ColorChecker Passport is photographed "under controlled lighting conditions" with the phone in automatic mode and without later editing or white balance (2015, 2016 and 2020 wording). A second chart at 1 lux exists from 2019, and from 2021 the text names the two setups "full studio lighting" and "1 lux". Light source type, colour temperature, illuminance of the bright setup for this chart and shooting distance are not stated in any page opened. The stated method did not change between 2015 and 2025. The presentation changed: up to 2018 the reference was burnt into the lower half of each patch, from 2019 it is an HTML box over the middle of each patch with a Delta E number.

### Cited Findings
- 2015, iPhone 6s review (published 2015-10-02). Caption: "iPhone 6S shot of the ColorChecker colors. We displayed the original colors in the lower half of every patch." Text: "We took a picture of the ColorChecker Passport under controlled lighting conditions to analyze the color accuracy and compared it with other current smartphones. We used the automatic mode of the smartphones and compared and analyzed the pictures without any editing (no white balance afterwards)." Sharpness chart: "We take a picture of the chart under defined artificial light for our sharpness test." ([iPhone 6s review](https://www.notebookcheck.net/Apple-iPhone-6S-Smartphone-Review.151403.0.html))
- 2016, Lumia 950 XL review (2016-01-27): "Screenshot of ColorChecker colors. The original color is displayed in the lower half of every patch." and "We only use the automatic mode of the smartphone, and the results are not edited afterwards." ([Lumia 950 XL review](https://www.notebookcheck.net/Microsoft-Lumia-950-XL-Smartphone-Review.158457.0.html))
- 2016 to 2018, same wording and layout. iPhone 7 (2016-09-18): "We use the ColorChecker Passport for the color accuracy." iPhone X (2017-11-24): "We check the color accuracy with the X-Rite ColorChecker passport." iPhone XS (2018-09-22), caption: "ColorChecker: The lower half of each area of color displays the reference color". All three say "under controlled lighting conditions". ([iPhone 7](https://www.notebookcheck.net/Apple-iPhone-7-Smartphone-Review.173851.0.html), [iPhone 8](https://www.notebookcheck.net/Apple-iPhone-8-Smartphone-Review.250930.0.html), [iPhone X](https://www.notebookcheck.net/Apple-iPhone-X-Smartphone-Review.264436.0.html), [iPhone XS](https://www.notebookcheck.net/Apple-iPhone-XS-Smartphone-Review.333546.0.html))
- 2019, iPhone 11 review (2019-09-20): first review in this set with the HTML overlay and a Delta E caption ("ColorChecker Apple iPhone 11: 10.1 ∆E min: 1.73 - max: 24.81 ∆E"). Its sharpness chart photos are captioned "A photograph of our test chart (2,500 lux)" and "A photograph of our test chart (1 lux)". This is the only illuminance figure for the bright setup found, and it is attached to the sharpness chart, not to the ColorChecker. ([iPhone 11 review](https://www.notebookcheck.net/Apple-iPhone-11-Smartphone-Review-More-than-just-an-affordable-Apple-smartphone.435087.0.html))
- 2020, Galaxy M11 review (2020-12-29): "the pictures of the X-Rite ColorChecker Passport (without post-processing, e.g. a manual white balance)". ([Galaxy M11 review](https://www.notebookcheck.net/Samsung-Galaxy-M11-Review-Very-affordable-Samsung-smartphone-with-terrible-cameras.512453.0.html))
- 2021, OnePlus 9 review (2021-04-03): "We use the ColorChecker Passport to analyze the color reproduction of the 48 MP lens in comparison to the actual reference colors. We take measurements once under full studio lighting and another time at 1 lux light intensity." ([OnePlus 9 review](https://www.notebookcheck.net/OnePlus-9-smartphone-review-Strong-OnePlus-phone-without-major-progress.530576.0.html))
- 2021 to 2024 iPhone reviews. 13 (2021-10-13): "Under controlled lighting conditions" and "at 1 lux". 15 Pro (2023-10-21): "we examined the main camera under both full studio lighting and extreme low-light conditions of only 1 lux". 16 (2024-10-28): "In the lab, we took another look at the main camera under full studio lighting and also at just 1 lux." ([iPhone 13](https://www.notebookcheck.net/Apple-iPhone-13-Smartphone-Review-High-quality-Apple-phone-with-an-even-brighter-display.572153.0.html), [iPhone 15 Pro](https://www.notebookcheck.net/Apple-iPhone-15-Pro-smartphone-in-review-High-end-phone-as-a-replacement-for-the-PlayStation.761070.0.html), [iPhone 16](https://www.notebookcheck.net/Apple-iPhone-16-smartphone-review-More-innovations-than-one-would-think.908118.0.html); text read from the copies saved on 2026-11-10)
- 2023 to 2025 Galaxy reviews give one sentence each. S23: "Under controlled lighting conditions, the Samsung Galaxy S23 shows decent color reproduction." S24 Ultra: "Under controlled lighting conditions, the Samsung Galaxy S24 Ultra achieves good color reproduction without any major outliers." S25: "In controlled lighting conditions, the Galaxy S25 showed pretty good color reproduction for a smartphone." None of the seven pages gives a lux value, a light source or a distance. "1 lux" appears only in the file name of the second chart. ([S23](https://www.notebookcheck.net/Samsung-Galaxy-S23-review-The-small-S23-smartphone-makes-the-biggest-leap.700018.0.html), [S24 Ultra](https://www.notebookcheck.net/Samsung-Galaxy-S24-Ultra-review-The-ultimate-smartphone-becomes-an-AI-smartphone.809187.0.html), [S25](https://www.notebookcheck.net/Samsung-Galaxy-S25-review-The-star-among-compact-smartphones-is-losing-ground.989246.0.html), [S25 Ultra](https://www.notebookcheck.net/Samsung-Galaxy-S25-Ultra-review-The-AI-phone-relies-fully-on-artificial-intelligence-and-small-hardware-upgrades.968346.0.html), [S24](https://www.notebookcheck.net/Samsung-Galaxy-S24-review-The-best-small-Android-smartphone-gets-cheaper-but-not-better.809474.0.html), [A55 5G](https://www.notebookcheck.net/Samsung-Galaxy-A55-5G-review-A-lot-of-premium-features-in-a-midrange-smartphone.835803.0.html), [A56 5G](https://www.notebookcheck.net/Loads-of-innovations-in-the-premium-mid-range-Samsung-Galaxy-A56-5G-review.1020512.0.html))
- Test criteria page (published 2024-10-28, modified 2026-10-05), camera section in full: "We test the front (webcam/selfie camera) and rear camera (main camera on smartphones) and evaluate the achievable image quality using standardized reference cards, as well as in direct practical comparison with current flagship devices. Image sharpness, color reproduction, contrast, light sensitivity, and video function are evaluated among other parameters." Example caption: "ColorChecker colors photographed. The original colors are shown in the lower half of each patch. The color deviations of the webcam are approximately DeltaE2000 9.1". It names no light source, illuminance, distance or camera setting. ([How does Notebookcheck test laptops and smartphones?](https://www.notebookcheck.net/Our-Test-Criteria.15394.0.html))
- Overlay markup in the 2023 to 2025 pages: one `<div class="colorchk_box" style="left:..;top:..;width:9%;height:13.5%;background-color:rgb(R,G,B)">` per patch with the Delta E value as text, and the rule `.colorchk_box:hover{background-color:transparent !important}`. Measured on the seven Galaxy charts, the box covers 68 to 69 % of the patch width and 67 to 68 % of its height and is centred (it spans from 14 to 20 % to 82 to 87 % of the patch height). The reference is an inset square with the phone colour around it, not a lower half. ([S25 review](https://www.notebookcheck.net/Samsung-Galaxy-S25-review-The-star-among-compact-smartphones-is-losing-ground.989246.0.html), [S23 review](https://www.notebookcheck.net/Samsung-Galaxy-S23-review-The-small-S23-smartphone-makes-the-biggest-leap.700018.0.html))
- The Galaxy chart files record the main camera (23 mm equivalent, digital zoom 1.0), exposure program 2 (normal program), exposure mode 0 (auto), white balance 0 (auto) and exposure bias 0. They were saved from Adobe Photoshop on Windows at 1365 to 2087 px width with the phone's P3 profile kept (Table 4a). The S25 and S25 Ultra charts were shot 26 seconds apart on 2025-02-19. ([S25](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25/Foto_GalaxyS25_ColorChecker.jpg), [S25 Ultra](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25_Ultra/Foto_GalaxyS25Ultra_ColorChecker.jpg), [S24](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24/Foto_GalaxyS24_ColorChecker.jpg), [S24 Ultra](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24_Ultra/Foto_GalaxyS24Ultra_ColorChecker.jpg), [S23](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S23/Foto_GalaxyS23_ColorChecker.jpg), [A55 5G](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A55_5G/Foto_GalaxyA55_ColorChecker.jpg), [A56 5G](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A56_5G/Foto_GalaxyA56_ColorChecker.jpg))
- EV100 from EXIF of the iPhone charts, for comparison (iPhone notes Table 4a): 7: 9.7, 8: 6.8, XS: 8.7, 11: 9.0, 13: 10.0, 15 Pro: 5.6, 16: 9.5. ([7](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_7/Foto_iPhone7_ColorChecker_Compare.jpg), [8](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_8/Calman.jpg), [XS](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_Xs/Foto_iPhoneXS_ColorChecker.jpg), [11](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_11/Foto_iPhone_11_ColoChecker.jpg), [13](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_13/Farbchart_high.jpg), [15 Pro](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_15_Pro/Farbchart_high.jpg), [16](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_16/Foto_iPhone16_ColorChecker.jpg))
- Access: robots.txt disallows only /nbcbattery/ and /cdn-cgi/. All pages and files were fetched on 2026-11-11 with curl's own user agent and returned HTTP 200. ([robots.txt](https://www.notebookcheck.net/robots.txt))

Table 4a. Exposure data stored in the chart files (EXIF). EV100 = log2(N^2/t) - log2(ISO/100).

| Chart | Shot at | Lens | Shutter | Aperture | ISO | BrightnessValue | EV100 | Exposure bias | Edited with |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| [S25](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25/Foto_GalaxyS25_ColorChecker.jpg) | 2025:02:19 13:32:03 | Galaxy S25, 5.4 mm (23 mm equiv.), zoom 1.0 | 1/119 s | f/1.8 | 25 | 5.54 | 10.6 | +0.00 | Adobe Photoshop 26.3 (Windows) |
| [S25 Ultra](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25_Ultra/Foto_GalaxyS25Ultra_ColorChecker.jpg) | 2025:02:19 13:32:29 | Galaxy S25 Ultra, 6.3 mm (23 mm equiv.), zoom 1.0 | 1/59 s | f/1.7 | 12 | 5.64 | 10.5 | +0.00 | Adobe Photoshop 26.3 (Windows) |
| [S24](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24/Foto_GalaxyS24_ColorChecker.jpg) | 2024:02:07 13:07:03 | Galaxy S24, 5.4 mm (23 mm equiv.), zoom 1.0 | 1/62 s | f/1.8 | 25 | 4.52 | 9.7 | +0.00 | Adobe Photoshop 25.4 (Windows) |
| [S24 Ultra](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24_Ultra/Foto_GalaxyS24Ultra_ColorChecker.jpg) | 2024:01:29 15:28:21 | Galaxy S24 Ultra, 6.3 mm (23 mm equiv.), zoom 1.0 | 1/54 s | f/1.7 | 16 | 5.20 | 9.9 | +0.00 | Adobe Photoshop 25.3 (Windows) |
| [S23](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S23/Foto_GalaxyS23_ColorChecker.jpg) | 2023:02:08 14:49:42 | Galaxy S23, 5.4 mm (23 mm equiv.), zoom 1.0 | 1/100 s | f/1.8 | 40 | 4.44 | 9.7 | +0.00 | Adobe Photoshop 24.1 (Windows) |
| [A55 5G](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A55_5G/Foto_GalaxyA55_ColorChecker.jpg) | 2024:03:27 16:20:50 | Galaxy A55 5G, 5.54 mm (23 mm equiv.), zoom 1.0 | 1/100 s | f/1.8 | 50 | 4.03 | 9.3 | +0.00 | Adobe Photoshop 25.6 (Windows) |
| [A56 5G](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A56_5G/Foto_GalaxyA56_ColorChecker.jpg) | 2025:04:11 13:42:22 | Galaxy A56 5G, 5.54 mm (23 mm equiv.), zoom 1.0 | 1/100 s | f/1.8 | 50 | 4.11 | 9.3 | +0.00 | Adobe Photoshop 26.4 (Windows) |
| [S25 (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25/Foto_GalaxyS25_ColorChecker_1Lux.jpg) | 2025:02:19 13:30:04 | Galaxy S25, 5.4 mm (23 mm equiv.), zoom 1.0 | 1/2 s | f/1.8 | 2000 | not stored | -1.6 | +0.00 | Adobe Photoshop 26.3 (Windows) |
| [S25 Ultra (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25_Ultra/Foto_GalaxyS25Ultra_ColorChecker_1Lux.jpg) | 2025:02:19 13:30:22 | Galaxy S25 Ultra, 6.3 mm (23 mm equiv.), zoom 1.0 | 1/2 s | f/1.7 | 3200 | not stored | -2.5 | +0.00 | Adobe Photoshop 26.3 (Windows) |
| [S24 (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24/Foto_GalaxyS24_ColorChecker_1Lux.jpg) | 2024:02:07 13:03:34 | Galaxy S24, 5.4 mm (23 mm equiv.), zoom 1.0 | 1/2 s | f/1.8 | 2000 | not stored | -1.6 | +0.00 | Adobe Photoshop 25.4 (Windows) |
| [S24 Ultra (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24_Ultra/Foto_GalaxyS24Ultra_ColorChecker_1Lux.jpg) | 2024:01:29 15:25:20 | Galaxy S24 Ultra, 6.3 mm (23 mm equiv.), zoom 1.0 | 1/2 s | f/1.7 | 3200 | not stored | -2.5 | +0.00 | Adobe Photoshop 25.3 (Windows) |
| [S23 (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S23/Foto_GalaxyS23_ColorChecker_1Lux.jpg) | 2023:02:08 14:47:51 | Galaxy S23, 5.4 mm (23 mm equiv.), zoom 1.0 | 1/2 s | f/1.8 | 2000 | not stored | -1.6 | +0.00 | Adobe Photoshop 24.1 (Windows) |
| [A55 5G (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A55_5G/Foto_GalaxyA55_ColorChecker_1Lux.jpg) | 2024:03:27 16:18:20 | Galaxy A55 5G, 5.54 mm (23 mm equiv.), zoom 1.0 | 1/4 s | f/1.8 | 2500 | not stored | -0.9 | +0.00 | Adobe Photoshop 25.6 (Windows) |
| [A56 5G (1 lux)](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A56_5G/Foto_GalaxyA56_ColorChecker_1Lux.jpg) | 2025:04:11 13:41:11 | Galaxy A56 5G, 5.54 mm (23 mm equiv.), zoom 1.0 | 1/4 s | f/1.8 | 2500 | not stored | -0.9 | +0.00 | Adobe Photoshop 26.4 (Windows) |

### Inferences
- The stated procedure is constant from 2015 to 2025 (same chart, automatic mode, no editing, lab light), so the charts are the same kind of measurement. The light level is not documented and was not constant: EV100 is 9.3 to 10.6 for the seven Galaxy charts and 8.7 to 10.0 for iPhone 7, XS, 11, 13 and 16, but 6.8 for iPhone 8 and 5.6 for iPhone 15 Pro, roughly 3 to 4 stops below the others (less light or another setup).
- Estimated illuminance from EV100 (E = 2.5 x 2^EV100 lux, incident meter constant 250, assumes the phone exposed like a meter): 1,600 to 3,900 lux for the Galaxy normal light charts and 0.4 to 1.3 lux for the 1 lux charts. Estimate, unverified. It agrees in magnitude with the labels "2,500 lux" and "1 lux".
- The charts fall into workflow groups. All seven Galaxy charts, iPhone 11 and iPhone 16 share the file naming (Foto_model_ColorChecker.jpg), the overlay template (first box at left 3.2 %, top 5.7 %, size 9.0 by 13.5 %) and one Photoshop version line (CC 2019, then 24.1, 25.3, 25.4, 25.6, 25.12, 26.3, 26.4). iPhone 13 and 15 Pro use Farbchart_high.jpg, Photoshop Elements 11 and other box positions (first box at 7.1/8.7 and 6.6/7.5 %). Galaxy rows therefore compare best with iPhone 16 and 11 and less well with iPhone 13 and 15 Pro.
- The layout change does not alter how the phone pixels are obtained. What differs for the old files: 6s and X carry no profile and no EXIF (sRGB had to be assumed), and composites were edited to paste the reference in.
- The reference is the nominal sRGB value of each patch, not a measurement of the lab's chart under the lab's light. Delta E therefore includes exposure and the unknown light, and it punishes a brighter rendering. Chroma ratio after white balance and hue shift after white balance depend least on the light and on exposure. White balance bias depends most on the unknown light.
- With no repeat shots, the size of a meaningful difference cannot be derived from these files. For scale: S25 and S25 Ultra, two phones shot 26 seconds apart, differ by 0.18 EV, 0 points of chroma and 4.3 points of B/G.

### Gaps
- Not stated in any page opened: light source type, colour temperature, colour rendering index, illuminance for the ColorChecker shot in the bright setup, distance, tripod use, resolution mode. Only Notebookcheck can settle these (not asked).
- The German originals on notebookcheck.com and older versions of the test criteria page were not opened.
- Between the XS review (2018-09-22, composite) and the iPhone 11 review (2019-09-20, overlay) no review was opened, so the month of the layout change is not pinned down.
- That Photoshop was used only to crop and resize is assumed from the stated policy. The files cannot prove it.

## Q5. Summary table

### Takeaway
Table 5 gives one row per Galaxy model in the columns of the iPhone table. Against the iPhone 16 row from the same chart workflow (chroma 150 %, mid grey +0.86 EV, mean Delta E 2000 10.6), every Galaxy chart is less saturated (117 to 136 %), darker at mid grey (-0.39 to +0.43 EV) and closer to the reference (4.0 to 9.5).

### Cited Findings
Table 5. One row per model, normal light chart. WB = linear R/G % and B/G % on neutral 8 to 3.5. Saturation = chroma ratio as measured, and after WB neutralisation. Hue shifts listed when 4 degrees or more (as measured).

| Model | WB bias on neutrals % | Saturation % (measured / after WB) | Tone | Mean dE00 | Hue shifts (degrees) |
| --- | --- | --- | --- | --- | --- |
| [S25](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25/Foto_GalaxyS25_ColorChecker.jpg) | R/G -5.5, B/G +0.8 (mixed, green) | 117 / 119 | EV -0.21, gamma 1.16, black L* 11 (ref 22), white L* 92 (ref 96) | 4.0 | light skin -12; foliage -5 |
| [S25 Ultra](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25_Ultra/Foto_GalaxyS25Ultra_ColorChecker.jpg) | R/G -3.5, B/G +5.1 (cool, no tint) | 117 / 117 | EV -0.39, gamma 1.18, black L* 10 (ref 22), white L* 89 (ref 96) | 4.9 | light skin -17; foliage -5 |
| [S24](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24/Foto_GalaxyS24_ColorChecker.jpg) | R/G +0.0, B/G +6.7 (mixed, magenta) | 131 / 129 | EV +0.33, gamma 0.86, black L* 20 (ref 22), white L* 87 (ref 96) | 5.9 | dark skin -5; light skin -17; foliage -8 |
| [S24 Ultra](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24_Ultra/Foto_GalaxyS24Ultra_ColorChecker.jpg) | R/G -2.0, B/G +2.4 (cool, no tint) | 122 / 123 | EV -0.18, gamma 1.13, black L* 12 (ref 22), white L* 87 (ref 96) | 5.2 | dark skin -7; light skin -16; blue +4; foliage -5 |
| [S23](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S23/Foto_GalaxyS23_ColorChecker.jpg) | R/G -3.7, B/G -0.5 (mixed, green) | 136 / 137 | EV +0.43, gamma 0.99, black L* 18 (ref 22), white L* 92 (ref 96), clipped: 7,9 | 9.5 | light skin -7; red +5 |
| [A55 5G](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A55_5G/Foto_GalaxyA55_ColorChecker.jpg) | R/G +5.5, B/G +7.4 (mixed, magenta) | 123 / 120 | EV +0.20, gamma 0.91, black L* 20 (ref 22), white L* 88 (ref 96), clipped: 16 | 5.4 | dark skin -11; light skin -15; blue sky -6; foliage -6; red +4 |
| [A56 5G](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_A56_5G/Foto_GalaxyA56_ColorChecker.jpg) | R/G +4.8, B/G +2.8 (mixed, magenta) | 122 / 120 | EV +0.15, gamma 0.86, black L* 26 (ref 22), white L* 89 (ref 96) | 5.4 | dark skin -5; light skin -10; blue sky -7; foliage -8; red +5 |

### Inferences
- iPhone 16 relative to Galaxy S25 (iPhone notes Table 5, measured 2026-11-10; Table 5 here, measured 2026-11-11): chroma 150 / 117 = 1.28 (after white balance 152 / 119 = 1.28); mid grey +0.86 against -0.21 EV, a gap of 1.07 EV; gamma 0.90 against 1.16; black patch L* 25 against 11; linear B/G -3.9 against +0.8 and R/G -1.6 against -5.5, so the iPhone grey is 4.7 points yellower and 3.9 points redder. ([iPhone 16 chart](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_16/Foto_iPhone16_ColorChecker.jpg), [S25 chart](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S25/Foto_GalaxyS25_ColorChecker.jpg))
- iPhone 16 relative to Galaxy S24: chroma 150 / 131 = 1.15; EV gap 0.53; gamma 0.90 against 0.86; black L* 25 against 20; B/G -3.9 against +6.7. ([iPhone 16 chart](https://www.notebookcheck.net/fileadmin/Notebooks/Apple/iPhone_16/Foto_iPhone16_ColorChecker.jpg), [S24 chart](https://www.notebookcheck.net/fileadmin/Notebooks/Samsung/Galaxy_S24/Foto_GalaxyS24_ColorChecker.jpg))
- Part of the chroma gap goes with the iPhone's brighter exposure. With lightness divided out (C*/(L*+16) after white balance, Table 3b in both notes) iPhone 16 is 130 against 122 for S25 (1.07) and 123 for S24 (1.06).
- Light skin shifts toward red in both brands, more on Galaxy: -7 to -17 degrees against -2 (iPhone 16) and -5 (iPhone 11) in the iPhone notes Table 3c.
- The Tone column of the iPhone table prints "ref 20" and "ref 97". The values computed from the standard patches are 21.7 (black 2) and 95.8 (white 9.5), as the header of the iPhone Table 3f shows. Table 5 here prints 22 and 96.

### Gaps
- The iPhone to Galaxy ratios compare charts shot on different days (iPhone 16: 2024-09-24, S24: 2024-02-07, S25: 2025-02-19). No session contains both brands.
- Hue shifts in Table 5 are listed from 4 degrees as measured. Values after white balance are in Table 3c.

<!-- AUTO-TABLES-END -->

## Handoff

Done: review and chart URLs for all seven models, downloads (28 image files, 7 review pages), overlay parsing, patch sampling with the unchanged `measure.py` logic, derived metrics with the unchanged `analyse()` of `metrics.py`, method text from 20 Notebookcheck pages (14 downloaded on 2026-11-11, 6 iPhone reviews read from the copies saved on 2026-11-10), summary table. No PENDING block is left.

Changes made to the copied scripts (originals untouched): model lists and URLs, the browser user agent removed from the two fetch scripts (curl's own user agent is sent), Table 2b to 2d rewritten for charts without a reference half, lens column filled from focal length because Samsung EXIF has no LensModel, reference L* in Table 5 computed instead of fixed text.

Remaining:
- Ask Notebookcheck for light source, colour temperature, illuminance and distance of the ColorChecker setup. Not published on https://www.notebookcheck.net/Our-Test-Criteria.15394.0.html.
- German originals not opened. Start at https://www.notebookcheck.com/ and search the model name.
- Month of the change from composite to overlay layout: open reviews dated between 2018-09-22 and 2019-09-20, for example https://www.notebookcheck.net/Samsung-Galaxy-S10-Smartphone-Review.410629.0.html (not opened).
- Other Galaxy models (S23 Ultra, S24+, S25+, S25 Edge, FE): add the review URL to `PAGES` in `fetch_pages.py`, the files to `fetch_all_images.sh` and `CONFIG` in `measure.py`, the labels to `MODELS` in `metrics.py`, then run the commands below.

Temporary files (outside the repository): `JOB_TMP/cc_galaxy/` with `pages/` (review HTML), `img/<model>/` (charts), `scripts/`, `overlays.json`, `out/measurements.json`, `out/narrative.json`, `out/debug_sheet.png` (normal light sampling windows), `out/debug_sheet_low.png` (1 lux).

Re-run everything (results land in `out/` of that folder and between the AUTO-TABLES markers of this file):

```
D=JOB_TMP/cc_galaxy
N="research_notes/아이폰 기종별 색감 수치 조사"
/opt/anaconda3/bin/python3 -I $D/scripts/fetch_pages.py $D/pages
sh $D/scripts/fetch_all_images.sh
/opt/anaconda3/bin/python3 -I $D/scripts/overlay_parse.py $D/pages $D/overlays.json s25 s25u s24 s24u s23 a55 a56
/opt/anaconda3/bin/python3 -I $D/scripts/measure.py $D/img $D/overlays.json $D/out
/opt/anaconda3/bin/python3 -I $D/scripts/make_narrative.py $D/out/narrative.json
/opt/anaconda3/bin/python3 -I $D/scripts/metrics.py $D/out/measurements.json "$N/galaxy_colorchecker_measurements.md" $D/out/narrative.json --quiet
cp $D/out/measurements.json "$N/galaxy_colorchecker_measurements.json"
```
