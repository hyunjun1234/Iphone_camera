# Pipeline changes and expert reviews of default iPhone photo rendering (6s to 16)

Research date: 2026-11-10. Status: partial, stopped early on a context limit. See Handoff at the end.

Reading rules for this file:
- Quotes were extracted by the fetch tool's summariser from the linked page. Spot-check wording before publishing.
- (S) means the claim is known only from a search-result summary. The page itself was not opened.
- All statements are reviewer impressions unless marked as a measurement. No reviewer gave Kelvin, percent saturation or histogram numbers.
- Sibling models are named in each line (6s Plus, 7 Plus, 8 Plus, XS Max, 11 Pro, 13 Pro, 14 Pro, 15 Pro Max, 16 Pro).

## 1. What did each generation's image pipeline change that affects colour and tone?

### Takeaway
Tone handling changed in three steps: optional HDR with a contrasty base image (6s, 7), always-on HDR that lifts shadows (8, X), and Smart HDR on nearly every frame with heavy noise reduction (XS). From the 11 onward the changes are per-region (semantic) edits, and from the 16 the user can turn the tone mapping down.

### Cited Findings
- 6s Plus: 12MP sensor replaced the 8MP one, and at 100% "detail looks smeared and much more processed" than the 6 Plus. HDR is a separate mode whose results "never" give "an unnatural or over-processed look". ([DPReview 6s Plus][dpr-6s])
- 7 Plus: "a faster f/1.8 lens (previously f/2.2)", and Wide Color capture means "the camera is actually capturing more color data". ([Austin Mann 7 Plus][am-7])
- 7 Plus: "the HDR mode delivers results that are practically identical to previous generations." ([DPReview 7 Plus][dpr-7])
- 8 Plus: "Apple's HDR effect in the iPhone 8 Plus is on by default and more aggressive than in previous generations." and "it's not exactly the same HDR effect used by previous devices." ([DPReview 8 Plus HDR][dpr-8])
- X: the 8 Plus is "capable of nearly identical image quality". ([DPReview X][dpr-x])
- XS: "Nearly every image you shoot on an iPhone XS or iPhone XS Max will have HDR applied to it." with "tone mapping that produces more even exposure and color". ([TechCrunch XS][tc-xs])
- XS, measurement from EXIF: "iPhone X exposed for 1/60th of a second at ISO 40 whereas the iPhone XS exposed for 1/120th of a second at ISO 80." Result: "iPhone XS has more aggressive noise reduction than previous iPhones." ([Lux XS][lux-xs])
- XS hardware: pixels "1.4 µm, compared to 1.22 µm for the iPhone X" ([DPReview XS][dpr-xs]). Sensor "32 percent larger", lens 26 mm equivalent versus 28 mm on the X ([Daring Fireball XS][df-xs]).
- 11: "This year's change to the imaging pipeline selectively edits every shot you ever take". It "can specifically target detail in skin texture, hair, and clothing." Deep Fusion arrived in iOS 13.2. ([Lux 11][lux-11])
- 11 Pro (S): Apple attributed the gains to a move from an 8-bit to a 10-bit rendering pipeline and to "semantic rendering", described as "basically an update to Smart HDR that recognizes individual elements of an image and adjusts them appropriately". Smart HDR was also made less aggressive. Source is a search excerpt of a syndicated copy of The Verge's review. ([MacSurfer copy][ms-11])
- 13: "Apple first introduced Photographic Styles with the iPhones 13", and "iPhones 13-15 will continue to use the first-generation Photographic Styles". ([Daring Fireball 16][df-16])
- 13 Pro: the pipeline "brightens faces while retaining texture in them" and "smoothes the sky and brings out color and clarity in the landscape". ([Lux 13 Pro][lux-13])
- 14 Pro (between 13 and 15 Pro): Smart HDR "'segments' human subjects" and applies "a fairly crude automatic lightness adjustment". ([Lux 14 Pro][lux-14])
- 15 Pro Max: "the iPhone camera now combines a 48 megapixel high-resolution frame with a 12 megapixel frame" to "create a highly detailed, high-resolution-but-manageable 24 MP shot by default." ([Lux 15 Pro Max][lux-15])
- 15 (S): more dynamic range and sharpness from Smart HDR 5 and the Photonic Engine are Apple's claims, reported as claims. ([DPReview 15 explainer][dpr-15])
- 16: new styles "can target undertones, the way the filter affects skin tones in your shots". Styles are reversible later. Photos "doesn't reduce sharpening, noise reduction, and subject-based adjustments." ([Lux 16 Pro][lux-16])
- 16 Pro: "The tone control is semantically aware" and "will adjust things like faces and the sky differently". Nilay Patel, quoted by Michael Tsai. ([mjtsai 2024-09-19][mjt-16])

### Inferences
- A single global LUT can imitate the base tone curve, white balance and saturation. It cannot imitate face relighting, sky smoothing or noise reduction, which reviewers name as the main changes from the XS onward.
- Shadow lift and highlight compression are the parameters with the best evidence across generations.

### Gaps
- Conflict on the 8 sensor: DPReview says "an image sensor with larger pixels" ([DPReview 8 Plus HDR][dpr-8]), GSMArena (S) says the same 1.22 µm pixel size ([GSMArena 8][gsm-8]). Unverified.
- Sensor-shift on the 13 and Smart HDR 4 details: no reviewer source read. Apple pages are in docs/RESEARCH.md.

## 2. What do in-depth reviewers say about each model's default colour and tone?

### Takeaway
6s, 7 and X are described as warm or punchy with clipped highlights and little shadow lift. The XS is described as flat. The 11 pulls back toward contrast. The 13 to 16 are described as bright, evenly lit and heavily denoised, with the 16 the flattest by default.

### Cited Findings
- 6s Plus: "images with slightly saturated and warm but pleasant colors". "the iPhone is prone to highlight clipping". "skin tones are natural". "Below ISO 400 luminance noise is clearly visible but pleasantly finely grained". "quite a bit of noise in the mid-tone areas." It "captures a slightly warmer image than the Motorola". ([DPReview 6s Plus][dpr-6s])
- 7 Plus: "punchy and pleasant colors". "some blown highlights are unavoidable". In HDR mode "Shadows are slightly lifted as well". "skin tones are natural and faces correctly exposed". "visible smearing of fine low-contrast detail". "in blue skies some luminance noise can be seen." Auto white balance "is doing a very good job in mixed light". ([DPReview 7 Plus][dpr-7])
- 7 Plus: "Colors in nature and skin tones are just gorgeous." ([Austin Mann 7 Plus][am-7])
- 7 series (S): GSMArena later wrote that "the dull, washed out colors we criticized the iPhone 7 series for are gone" on the 8. This conflicts with DPReview's "punchy". ([GSMArena 8][gsm-8])
- 8 Plus: "more graduated tones", "a better job of filling in shadows", and HDR "restored detail in the clouds and often presented blue skies where they weren't visible in the preview." Result is "balanced and realistic". ([DPReview 8 Plus HDR][dpr-8])
- 8 Plus, HDR on versus off in harsh sun on a white shirt: "In image 1, there's zero detail in parts of his shirt. In image 2, it's full of detail." ([Austin Mann 8 Plus][am-8])
- 8 (S): "Apple has definitely tweaked colors to increase the saturation compared to the iPhone 7" ([Digital Trends 8][dt-8]). Contrast "superb", colours true to life, watercolour-like foliage at 1:1 ([GSMArena 8][gsm-8]).
- X: "colors are nicely punchy and saturated". "White balance strays to the warm side". "sometimes colors can skew warmer than when HDR is disabled." Photos "can be overexposed with blown highlights". "There doesn't seem to be much of anything in the way of shadow lifting." "there's a fairly abrupt clipping point where it all blows out to white". "really large-radius sharpening that looks exceedingly artificial". Dark areas take "a painterly look". Skin shows "what looks like cloned-in skintones over hot-spots". ([DPReview X][dpr-x])
- XS: "This is a whole new look, a drastic departure from the 'look' of every iPhone before it." It "reduces the brightness of the bright areas and reduces the darkness of the shadows." Detail is perceived "as less sharp because it lost local contrast." "the skin looks smoother simply because the light isn't as harsh." ([Lux XS][lux-xs])
- XS: "the iPhone XS is so good at balancing light, it's actually quite difficult to create a silhouette!" Caveat: "Perhaps the tones in the sky don't look quite right". ([Austin Mann XS][am-xs])
- XS: "The iPhone XS image almost looks as if it was shot using an off-camera flash". ([DPReview XS][dpr-xs])
- XS and XR, looking back from 2019: "Smart HDR was heavy-handed at times, smoothing out texture", "refusing to let an overexposed shot just be overexposed", and "it became very easy to spot images shot with the iPhone XS and XR by their 'look' alone." ([Lux 11][lux-11])
- 11: "On the iPhone 11, the camera knows that sometimes you want highlights blown out." "Images just looks sharper and smoother." Dynamic range "around 10 stops" (context of the figure not captured, unverified). ([Lux 11][lux-11])
- 11 (S): colours accurate but flat, contrast "could have been better", over-sharpened in complex detail, "bland camera photos" listed as a con. ([GSMArena 11][gsm-11])
- 13 Pro: "the iPhone's processing on iPhone 13 Pro is simply too heavy-handed at times." Results show "a 'watercolor'-like effect" and leaves that "look smudged". ([Lux 13 Pro][lux-13])
- 13 (S): "great contrast", very low noise, dynamic range "not the best" but balanced, photos basically identical to the 12. ([GSMArena 13][gsm-13])
- 14 Pro, the model before the 15 Pro: it "always tries to lighten the shadows, especially on people's faces, making the photo look very artificial" and "exaggerates the sharpness". This is 9to5Mac's paraphrase of Marques Brownlee. ([9to5Mac][9to5-mkbhd])
- 14 Pro era: "The iPhone's 'thing' is: when you take a photo, it likes to identify faces and evenly light them." Brownlee, quoted by Nick Heer. ([Pixel Envy][pxlnv-23])
- 15 Pro Max: "colors are ever so slightly more pleasant" than the 14 Pro. ProRAW keeps "a bit more noise" where earlier generations gave "that smudged, 'watercolor' effect". Reported complaint: "People in photos are blown out and seemingly over-processed". ([Lux 15 Pro Max][lux-15])
- 16 Pro: "by default, the iPhone 16 Pro camera is even more aggressive about evening out shadows and highlights than the iPhone 15 Pro" and "it's all just a little bit brighter and a little bit flatter." Nilay Patel, quoted by Michael Tsai. ([mjtsai 2024-09-19][mjt-16])
- 16 Pro: "I found it difficult to spot a difference between iPhone 15 Pro and iPhone 16 Pro captures." Styles "still give your photos a 'Shot on iPhone' look". ([Lux 16 Pro][lux-16])
- 16, Standard style: "processes images in a way that looks, well, iPhone-y". Standard processing has "Too much noise reduction, too much smoothing. A little too punchy." ([Daring Fireball 16][df-16])
- 16 (S): colours "noticeably muted and lack any 'pop'" but natural, strong dynamic range, well-developed shadows. ([GSMArena 16][gsm-16])
- 16 Pro tone control: "Pushing up generally brightens the image and gives it a more balanced, 'processed' look" and "pushing down creates a more filmic feel." ([Austin Mann 16 Pro][am-16])

### Inferences
- Reviewer wording supports this order for shadow lift, lowest to highest: 6s and 7 (HDR optional), X (little lifting), 8 (lifting with default HDR), 11, 13 to 15 Pro, 16, with the XS as the outlier that reviewers call the flattest relative to its neighbours.
- "Punchy" is used for the 7, X and for Standard on the 16, so saturation does not fall steadily over time. The GSMArena summaries point to muted colour on the 11 and 16.

### Gaps
- No reviewer source gives a white balance direction for the 11, base 13, 15 Pro or 16. DXOMARK pages listed in docs/COLOR_EVIDENCE.md were not re-read.
- Base 13 and base 16 evidence comes from search summaries only. 15 Pro evidence is from the 15 Pro Max and the 14 Pro.
- Sky and foliage statements are missing for the 6s, 15 Pro and 16.

## 3. What do old-versus-new comparisons say?

### Takeaway
The best-documented steps are 7 to 8 (more saturation, lifted shadows), X to XS (less yellow, lower contrast, highlights held), XS to 11 (contrast and vivid colour return) and 15 Pro to 16 Pro (slightly brighter and flatter). No direct 6s versus 15 Pro or 11 versus 13 comparison was found.

### Cited Findings
- 7 Plus versus 8 Plus: in bright scenes "the iPhone 7 Plus pulled more toward reducing exposure throughout", while "the iPhone 8 Plus is also lifting the shadows". ([DPReview 8 Plus HDR][dpr-8])
- 7 versus 8 (S): 8 images "tend to be more saturated, giving them extra pop", and the 8 "is less prone to blowout than the iPhone 7, and images exhibit better contrast". ([WhistleOut][wo-8])
- X versus XS: "The iPhone X photo has a yellowish hue, but the XS' photo is bluer and it looks more natural." "the sky is also a tad bluer, and the clouds more defined." "The iPhone X photo looks slightly overexposed overall". On the XS "there also seems to be a slight dip in contrast". XS "skin tone is much more realistic and natural". ([Digital Trends X vs XS][dt-xs])
- X versus XS, photo captions: XS has "better range of tones, skintone and black point". The X shows "blocking up of shadow detail, flatter skin tone and blue shift". The "blue shift" conflicts with Digital Trends' "yellowish hue", so the cast is scene dependent. ([TechCrunch XS][tc-xs])
- X versus XS: "The iPhone X image is blown out; the iPhone XS image looks pretty good." ([Daring Fireball XS][df-xs])
- X versus XS: "the XS makes the X camera look terrible most of the time", but "I think the Pixel 2 still has a better camera than the iPhone XS." Nilay Patel, quoted by MacRumors. ([MacRumors XS roundup][mr-xs-round])
- XS Max versus 11 Pro Max: the 11 Pro Max "is sharper in some situations with more vivid coloring", yet "certain areas of the image get more blown out than with the iPhone XS Max." In good light "there isn't a lot of difference". ([MacRumors comparison][mr-11])
- XS versus 11 Pro, 9to5Mac's paraphrase: Patel criticised XS Max Smart HDR for reduced contrast and flat colours, TechCrunch found better contrast and colour balance on the 11 Pro than the "flat" XS photos, and WIRED criticised colour accuracy. ([9to5Mac roundup][9to5-11])
- XS versus 11 Pro (S): "Smart HDR on the XS reduced the contrast giving photos a weird flat look", the XS sky is "this weird shade of blue that's flat", and the 11 Pro sky is "more true to life". ([AppleInsider][ai-11])
- 13 Pro versus 14 Pro: "the processing and image quality on the iPhone 14 Pro is simply leagues ahead", "Detail and color are far superior", and "iPhone 13 Pro would rank up with the iPhone XS in our iPhone camera lineup". ([Lux 14 Pro][lux-14])
- 13 Pro versus 15 Pro (S): Apple fixed a bluish white balance seen on older iPhones, and the 15 Pro "captures more realistic and warmer colors". ([PhoneArena][pa-15v13])
- 15 versus 16 (S): "hardly any improvement in the daylight photo quality". ([GSMArena 16 vs 15][gsm-16v15])
- 7 versus 12 and 13 Pro: "Separate iPhones by five years, though, and the results are striking." John Gruber on John Nack's paired photos. ([Daring Fireball][df-nack])
- Blind test, 2018: X and XS were both "knocked out in the first round". "voters chose the Key2 image as it was brighter and warmer than the XS shot." "a big part of how people voted came down to the color and warmth of the images." ([9to5Mac][9to5-blind18])
- Blind tests, later (S): the 11 Pro lost in the first round in 2019 ([iMore][imore-blind]). The 14 Pro placed 6th in the 2022 standard test ([9to5Mac][9to5-blind22]).

### Inferences
- Suggested direction of parameter change, to be checked against photos: 7 to 8 raise saturation and shadows. X to XS lower contrast, raise shadows, compress highlights, cool the white balance slightly. XS to 11 raise contrast and saturation and relax highlight compression. 15 Pro to 16 raise midtone brightness and lower contrast slightly.
- Blind-test losses of the XS, 11 Pro and 14 Pro suggest the iPhone default was less bright and warm than what voters preferred.

### Gaps
- No direct 6s versus 15 Pro, 11 versus 13 or 8 versus X photo comparison with colour statements was found.
- The 13 Pro to 15 Pro warm shift rests on one search summary.

## 4. Why are older iPhone photos (6s, 7, 8, X, XS) called nostalgic or film-like?

### Takeaway
Reviewer descriptions of the 6s, 7 and X match the traits people list: warm colour, clipped highlights, deep shadows, intact local contrast and fine grain. The XS does not fit this group. It is the first of the flat, heavily denoised generations.

### Cited Findings
- Warm colour: 6s Plus "slightly saturated and warm" ([DPReview 6s Plus][dpr-6s]). X "White balance strays to the warm side" ([DPReview X][dpr-x]).
- Clipped highlights and deep shadows: X has "a fairly abrupt clipping point" and not "much of anything in the way of shadow lifting" ([DPReview X][dpr-x]). 6s Plus "is prone to highlight clipping" ([DPReview 6s Plus][dpr-6s]).
- Grain: 6s Plus noise is "pleasantly finely grained" ([DPReview 6s Plus][dpr-6s]). XS noise "isn't the gentle, film-like grain we previously saw in iPhone X and iPhone 8 RAW files." ([Lux XS][lux-xs])
- Local contrast: lost on the XS when exposures are merged, present before it. ([Lux XS][lux-xs])
- What an unprocessed single frame looks like: "Much like film, it has grain." "It will be less saturated, softer, grainier". "you are limited by the dynamic range of the sensor." ([Lux Process Zero][lux-pz])
- Modern look by contrast: "pictures taken on modern iPhones often look sort-of washed out and samey", "like much of the contrast and highlights from real life were lost somewhere along the way during HDR processing". Federico Viticci, quoted by Michael Tsai. ([mjtsai][mjt-over])
- User sentiment: "The last good iPhone camera I remember using was my iPhone X." Tanner Bennett, 2024. ([mjtsai][mjt-over])
- Imperfections valued: Nick Heer lists "white balance that is way too warm" among things that, when smoothed out, "can make it feel sterile and inhuman". ([Pixel Envy][pxlnv-23])
- "pushing down" the 16 tone control "creates a more filmic feel", which ties "filmic" to less shadow lift. ([Austin Mann 16 Pro][am-16])
- Trend reports (S): the old-iPhone trend followed the digicam craze and covers the iPhone 4 to 6s, called by some the "sweet spot" of processing ([PhoneArena][pa-2026]). Gen Z reuse old iPhones for photos ([Vogue Singapore][vogue]). The Verge says people carry old iPhones for a simpler, less processed look ([The Verge post][verge-bsky]). A forum post attributes the appeal to "more natural contrast, less aggressive HDR processing, and softer detail rendering" ([MacRumors forum][mr-classic]).

### Inferences
- For the 6s, 7 and X presets the evidence supports: warm white balance, a steeper curve with deeper blacks, early highlight clipping, slightly higher saturation and added fine luminance grain. Lifted blacks and lowered saturation are not supported by the reviews read. The current 6s preset description in docs/RESEARCH.md (lowered saturation, lifted black) points the other way from DPReview's "slightly saturated and warm".
- For the XS the supported direction is low contrast with lifted shadows and compressed highlights.

### Gaps
- No source measures the traits. The trend articles were not opened, so their reasoning is unverified.
- Green or magenta tint: no reviewer statement found for any model.

## 5. Per-model summary table

### Takeaway
Cells cite the source key from the reference list. "unverified" means no source read supports a value.

### Cited Findings

| Model (evidence from) | WB tendency | Saturation | Contrast | Shadows | Highlights | Skin | Sky and greens | Strongest differentiator |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 6s (6s Plus) | slightly warm [dpr-6s] | slightly saturated [dpr-6s] | unverified | no default lifting, HDR optional [dpr-6s] | prone to clipping [dpr-6s] | natural [dpr-6s] | unverified | warm, fine grain, mid-tone noise [dpr-6s] |
| 7 (7 Plus) | no cast reported, good auto WB [dpr-7] | punchy [dpr-7], washed out per (S) [gsm-8] | unverified | slight lift only in HDR mode [dpr-7] | some blown, exposure pulled down in bright scenes [dpr-7] [dpr-8] | natural, faces correctly exposed [dpr-7] | noise in blue sky [dpr-7] | wide colour capture, f/1.8 [am-7] |
| 8 (8 Plus, 8) | better than 7, direction unverified (S) [gsm-8] | higher than 7 (S) [dt-8] [wo-8] | superb (S) [gsm-8] | filled in by default HDR [dpr-8] | clouds recovered, graduated tones [dpr-8] | unverified | blue sky restored [dpr-8], watercolour foliage (S) [gsm-8] | first always-on HDR [dpr-8] |
| X | warm, warmer with HDR [dpr-x], yellowish vs XS [dt-xs], blue shift in one pair [tc-xs] | punchy and saturated [dpr-x] | higher than XS [dt-xs] | little lifting [dpr-x], blocked up [tc-xs] | blown, abrupt clipping [dpr-x] [df-xs] | hot spots [dpr-x], flatter than XS [tc-xs] | sky overexposed [dt-xs] | last contrasty look before Smart HDR [lux-xs] |
| XS (XS, XS Max) | bluer than X [dt-xs] | flat colours per paraphrase [9to5-11] | lowest, lost local contrast [lux-xs] [dt-xs] | strongly lifted [lux-xs] [am-xs] | strongly held [am-xs] [lux-11] | smoother, more natural tone than X [lux-xs] [dt-xs] | bluer sky [dt-xs], flat blue sky (S) [ai-11] | flat Smart HDR look, heavy noise reduction [lux-xs] |
| 11 (11, 11 Pro) | unverified | more vivid than XS Max [mr-11], flat per (S) [gsm-11] | more than XS [9to5-11] | lifted less than XS (S) [ms-11] | sometimes left blown [lux-11] [mr-11] | targeted by semantic rendering [lux-11] | sky more true to life than XS (S) [ai-11] | semantic rendering, sharper [lux-11] |
| 13 (13 Pro, 13) | bluish relative to 15 Pro (S) [pa-15v13] | unverified | great contrast (S) [gsm-13] | faces brightened [lux-13] | balanced, not the widest range (S) [gsm-13] | brightened, texture kept [lux-13] | sky smoothed, landscape colour boosted, leaves smudged [lux-13] | heavy smoothing, first Photographic Styles [lux-13] [df-16] |
| 15 Pro (15 Pro Max, 14 Pro) | warmer than 13 Pro (S) [pa-15v13] | slightly more pleasant than 14 Pro [lux-15] | unverified | lifted, most on faces (14 Pro) [9to5-mkbhd] | unverified | people can look blown out [lux-15] | unverified | 24MP default, less smudging [lux-15] |
| 16 (16 Pro, 16) | unverified | muted (S) [gsm-16], punchy per Gruber [df-16] | slightly flatter than 15 Pro [mjt-16] | evened out more than 15 Pro [mjt-16] | evened out [mjt-16] | protected by undertone control [lux-16] | weak foliage at 12MP (S) [gsm-16v15] | brightest and flattest default, tone control reverses it [mjt-16] [am-16] |

### Inferences
- Columns with enough evidence to set relative parameters: shadows and highlights for all models, contrast for X, XS and 11, white balance for 6s, X and XS, saturation for 6s, 7, 8 and X.
- Conflicting cells (7 saturation, 11 saturation, 16 saturation, X cast) should stay near neutral until measured.

### Gaps
- Unverified cells above. Tint (green or magenta) is unverified for every model.

## Handoff

Done:
- Lux/Halide posts for XS, 11, 13 Pro, 14 Pro, 15 Pro Max, 16 Pro and Process Zero.
- DPReview for 6s Plus (conclusion page), 7 Plus, 8 Plus HDR, X, XS.
- Austin Mann for 7 Plus, 8 Plus, XS, 16 Pro. Daring Fireball for XS, 11, 16. TechCrunch XS. Digital Trends X vs XS. MacRumors 11 Pro Max vs XS Max and two review roundups. 9to5Mac, mjtsai and Pixel Envy pages on over-processing.

Remaining, in priority order:
1. White balance direction for 11, 13, 15 Pro, 16: re-read the DXOMARK pages listed in docs/COLOR_EVIDENCE.md for colour cast wording.
2. Austin Mann 6s, 11 Pro and 15 Pro Max reviews. Guessed URLs returned 404. Find the links from https://pxlnv.com/linklog/mann-iphone-11-pro/ and https://www.macrumors.com/2021/09/22/austin-mann-iphone-13-pro-camera-test/ or search "austinmann.com trek iphone 15 pro max".
3. PetaPixel 15 Pro review: https://petapixel.com/2023/09/19/the-iphone-15-pro-and-pro-max-review-for-photographers/
4. Digital Trends: https://www.digitaltrends.com/mobile/iphone-11-pro-vs-iphone-xs-camera-shootout/ , https://www.digitaltrends.com/mobile/iphone-15-pro-vs-iphone-13-pro/ , https://digitaltrends.com/mobile/apple-iphone-8-review , and search "digitaltrends iphone 16 pro vs iphone 15 pro camera comparison".
5. DPReview image-quality pages: https://www.dpreview.com/news/1603942322/iphone-7-plus-camera-review/6 and https://www.dpreview.com/reviews/7381192801/apple-iphone-6s-plus-camera-review/ , plus [dpr-15].
6. TechCrunch 11 Pro review by Matthew Panzarino: search "Panzarino iPhone 11 Pro review techcrunch 2019".
7. Old versus new: search "iPhone 6s vs iPhone 16 camera comparison photos", "iPhone X vs iPhone 15 Pro camera comparison", "iPhone 11 vs iPhone 13 camera comparison tomsguide".
8. Trend articles not opened: [vogue], https://www.creativebloq.com/design/someone-photographed-apples-hq-with-the-very-first-iphone-and-people-think-the-shots-look-better-than-those-taken-with-the-iphone-17-pro , https://www.macworld.com/article/3111956/how-im-taking-much-better-photos-with-my-iphone.html
9. Blind-test write-ups not opened: [imore-blind], [9to5-blind22], https://9to5mac.com/2020/12/02/iphone-12-pro-max-loses-to-oneplus-8t-in-mkbhds-blind-smartphone-camera-test/
10. Not started: Lux iPhone 12 Pro Max and 17 Pro reviews, Ars Technica reviews.

Could not open:
- theverge.com: the site blocks this agent's user agent in both the fetch and the search tool. Do not work around it with curl. A person can open these in a browser: https://www.theverge.com/2019/9/17/20868727/apple-iphone-11-pro-max-review-camera-battery-life-screen-midnight-green-price , https://www.theverge.com/24247538/apple-iphone-16-pro-review , https://www.theverge.com/2017/9/19/16323570/apple-iphone-8-review-plus-camera-screen-battery-price . The XS review URL tried returned 404.
- newyorker.com (Kyle Chayka, "Have iPhone Cameras Become Too Smart?"): blocked for the fetch tool. Only [df-chayka] and [df-nack] were read.
- gsmarena.com review pages: redirect to a paid bot gateway (tollbit). Content above is from search summaries. Pages: [gsm-8], [gsm-11], [gsm-13], [gsm-16], [gsm-16v15], https://www.gsmarena.com/apple_iphone_xs-review-1827p6.php
- phonearena.com ([pa-15v13], [pa-2026]) and forums.appleinsider.com ([ai-11]): HTTP 403.
- web.archive.org: blocked for the fetch tool. anandtech.com articles redirect to the forum home page.
- galaxus.ch iPhone 16 Pro review (https://www.galaxus.ch/en/page/iphone-16-pro-im-test-rueckkehr-der-schatten-34908): timeout.

[lux-xs]: https://www.lux.camera/iphone-xs-why-its-a-whole-new-camera/
[lux-11]: https://www.lux.camera/inside-the-iphone-11-camera-part-1-a-completely-new-camera/
[lux-13]: https://lux.camera/iphone-13-pro-camera-app-intelligent-photography/
[lux-14]: https://www.lux.camera/iphone-14-pro-camera-review-a-small-step-a-huge-leap/
[lux-15]: https://www.lux.camera/iphone-15-pro-max-review/
[lux-16]: https://www.lux.camera/the-iphone-16-pro-camera-review-control/
[lux-pz]: https://www.lux.camera/introducing-process-zero-for-iphone/
[dpr-6s]: https://www.dpreview.com/reviews/apple-iphone-6s-plus-camera-review/7
[dpr-7]: https://www.dpreview.com/reviews/1603942322/iphone-7-plus-camera/
[dpr-8]: https://www.dpreview.com/articles/4326186527/hdr-is-enabled-by-default-on-the-iphone-8-plus-and-that-s-a-really-good-thing/
[dpr-x]: https://www.dpreview.com/reviews/4941827141/iphone-x-review/
[dpr-xs]: https://www.dpreview.com/articles/5831987965/the-iphone-xs-is-a-leap-forward-in-computational-photography/
[dpr-15]: https://www.dpreview.com/articles/0399301311/iphone-15-cameras-what-you-need-to-know/
[am-7]: https://www.austinmann.com/trek/iphone-7-camera-review-rwanda
[am-8]: https://www.austinmann.com/trek/iphone-8-camera-review-india
[am-xs]: https://www.austinmann.com/trek/iphone-xs-camera-review-zanzibar
[am-16]: https://www.austinmann.com/trek/iphone-16-pro-camera-review-kenya
[tc-xs]: https://techcrunch.com/2018/09/18/review-iphone-xs-and-the-power-of-long-term-thinking/
[dt-xs]: https://www.digitaltrends.com/photography/iphone-xs-and-xs-max-vs-iphone-x-camera-shootout/
[dt-8]: https://digitaltrends.com/mobile/apple-iphone-8-review
[df-xs]: https://daringfireball.net/2018/09/the_iphones_xs
[df-16]: https://daringfireball.net/2024/09/the_iphones_16
[df-nack]: https://daringfireball.net/linked/2022/03/23/nack-iphone-7-vs-iphone-12
[df-chayka]: https://daringfireball.net/linked/2022/03/22/chayka-iphone-smart
[mr-11]: https://www.macrumors.com/2019/09/25/iphone-11-pro-max-vs-xs-max
[mr-xs-round]: https://www.macrumors.com/2018/09/18/iphone-xs-and-xs-max-review-roundup
[mr-classic]: https://forums.macrumors.com/threads/classic-iphone-camera-styles.2482979/
[9to5-11]: https://9to5mac.com/2019/09/17/iphone-11-pro-review-roundup/
[9to5-mkbhd]: https://9to5mac.com/2023/01/06/mkbhd-post-processing-ruining-iphone-photos/
[9to5-blind18]: https://9to5mac.com/2018/12/04/iphone-x-xs-blind-camera-test/
[9to5-blind22]: https://9to5mac.com/2022/12/22/mkbhd-blind-camera-test-iphone-14-pro-results/
[imore-blind]: https://www.imore.com/blind-phone-camera-test-sees-iphone-11-pro-knocked-out-first-round
[mjt-16]: https://mjtsai.com/blog/2024/09/19/
[mjt-over]: https://mjtsai.com/blog/?p=38098
[pxlnv-23]: https://pxlnv.com/blog/what-phone-cameras-see-in-2023/
[ms-11]: https://www.macsurfer.com/redir.php?u=1140934
[ai-11]: https://forums.appleinsider.com/discussion/213023
[gsm-8]: https://www.gsmarena.com/apple_iphone_8-review-1664p8.php
[gsm-11]: https://www.gsmarena.com/apple_iphone_11-review-1993p7.php
[gsm-13]: https://www.gsmarena.com/apple_iphone_13-review-2325p5.php
[gsm-16]: https://www.gsmarena.com/apple_iphone_16-review-2749p5.php
[gsm-16v15]: https://www.gsmarena.com/apple_iphone_16_vs_iphone_15_review_battery_camera_price_compared-news-64910.php
[wo-8]: https://www.whistleout.com.au/MobilePhones/Guides/iphone-8-camera-vs-iphone-7-camera
[pa-15v13]: https://www.phonearena.com/reviews/iPhone-15-Pro-vs-iPhone-13-Pro-what-has-changed_id5803
[pa-2026]: https://www.phonearena.com/news/In-2026-Samsung-Google-and-Apple-need-to-do-this_id176934
[vogue]: https://vogue.sg/iphone4/
[verge-bsky]: https://bsky.app/profile/theverge.com/post/3msvcl6rgoc2y
