#!/usr/bin/env python3
"""Derive the nine style values in app/src/main/java/studio/moment/camera/color/Look.kt from chart measurements.

Input: colorchecker.json next to this file. It holds the colour of the 24 ColorChecker patches in the chart photos
of nine iPhones and seven Galaxy phones. A style is the change that turns the average Galaxy rendering of the chart
into the iPhone's rendering, expressed with the seven values of the app's colour model:

  1. tone (gamma, contrast, fade, highlight)  six grey patches
  2. white balance (warmth, tint)             linear R/G and B/G on grey patches 20 to 23
  3. saturation                               chroma of the 18 colour patches after white balance, lightness divided out

Three charts do not show the main camera in normal light and are replaced:
  iPhone 8       tone from the iPhone X chart (same sensor generation; the 8 chart was shot in about 3 stops less light)
  iPhone 13      white balance halfway to the typical iPhone value (chart from another setup)
  iPhone 15 Pro  tone and saturation from the mean of 13 and 16, typical white balance (ultra wide lens, dim light)

Two choices are not measurements and can be changed on the command line:
  --shared 0.5   share of the brightness difference that all iPhones have in common against Galaxy. The seven
                 Galaxy charts differ from each other by up to 0.7 EV at mid grey, so how much brighter an iPhone
                 is depends on the Galaxy; half of it is used. Differences between iPhone models are kept in full.
  --target 12.5  strength. A style that changes the chart little is scaled up and a strong one down:
                 gain = sqrt(target / change), limited to 0.85 .. 1.6. change is the mean CIE76 colour difference
                 the unscaled style makes on the Galaxy chart. --target 0 switches the scaling off.

Run:  python3 scripts/color/derive_looks.py            (needs numpy and scipy)
Output: a table and the value lines for Look.kt on the terminal. --json FILE also writes the values to FILE,
--markdown adds the table used in docs/COLOR_EVIDENCE.md (differences before the gain, then the applied values).
"""
import json
import sys
from pathlib import Path

import numpy as np
from scipy.optimize import brentq, least_squares

IPHONES = ['6s', '7', '8', 'X', 'XS', '11', '13', '15pro', '16']
TYPICAL_WB = ['7', '8', 'X', '11', '16']      # charts whose grey balance sits close together
TONE_FROM = {'8': ['X'], '15pro': ['13', '16']}
SATURATION_FROM = {'15pro': ['13', '16']}
NAMES = ['warmth', 'tint', 'saturation', 'contrast', 'fade', 'highlight', 'gamma']
GREYS = [23, 22, 21, 20, 19, 18]              # black to white
WB_PATCHES = [19, 20, 21, 22]
TONE_LOW, TONE_HIGH = np.array([0.70, 0.85, 0.0, -0.02]), np.array([1.35, 1.35, 0.05, 0.04])
LUMA = np.array([.2126, .7152, .0722])
TO_XYZ = {'srgb': np.array([[0.4123907993, 0.3575843394, 0.1804807884],
                            [0.2126390059, 0.7151686788, 0.0721923154],
                            [0.0193308187, 0.1191947798, 0.9505321522]]),
          'p3': np.array([[0.4865709486, 0.2656676932, 0.1982172852],
                          [0.2289745641, 0.6917385218, 0.0792869141],
                          [0.0000000000, 0.0451133819, 1.0439443689]])}
WHITE = TO_XYZ['srgb'].sum(axis=1)


def decode(encoded):
    """sRGB transfer curve, 0..1 encoded to linear."""
    v = np.asarray(encoded, float)
    return np.where(v <= 0.04045, v / 12.92, ((np.abs(v) + 0.055) / 1.055) ** 2.4)


def encode(linear):
    v = np.clip(np.asarray(linear, float), 0, None)
    return np.where(v <= 0.0031308, 12.92 * v, 1.055 * v ** (1 / 2.4) - 0.055)


def lab_of_linear(linear):
    t = (linear @ TO_XYZ['srgb'].T) / WHITE
    f = np.where(t > (6 / 29) ** 3, np.cbrt(np.clip(t, 0, None)), t / (3 * (6 / 29) ** 2) + 4 / 29)
    return np.stack([116 * f[..., 1] - 16, 500 * (f[..., 0] - f[..., 1]), 200 * (f[..., 1] - f[..., 2])], axis=-1)


def lab(srgb):
    return lab_of_linear(decode(srgb))


def lightness(grey):
    """L* of a neutral with the given encoded value."""
    y = decode(grey)
    return 116 * np.where(y > (6 / 29) ** 3, np.cbrt(y), y / (3 * (6 / 29) ** 2) + 4 / 29) - 16


def s_curve(v, contrast):
    """Contrast that keeps black and white in place: slope `contrast` at mid grey, a soft toe and shoulder."""
    v = np.clip(v, 0, 1)
    return np.where(v < .5, .5 * np.power(2 * v, contrast), 1 - .5 * np.power(2 * (1 - v), contrast))


def grade(rgb, p):
    """The app's style maths at full intensity (ColorLut.create). rgb: encoded sRGB 0..1, shape (n, 3)."""
    warmth, tint, saturation, contrast, fade, highlight, gamma = p
    r, g, b = rgb[:, 0], rgb[:, 1], rgb[:, 2]
    luma = rgb @ LUMA
    skin = np.clip(1 - np.abs((r - g) - .12) * 5, 0, 1) * np.clip((g - b) * 8, 0, 1) * np.clip((luma - .12) * 3, 0, 1)
    amount = saturation + (1.0 - saturation) * skin * .65

    def channel(c, balance):
        v = luma + (c - luma) * amount
        v = np.clip(v + balance * (1 - np.abs(v - .5)), 0, 1)
        v = s_curve(v ** gamma, contrast)
        v = v + fade * (1 - v) ** 2
        return np.clip(v - highlight * v ** 4, 0, 1)

    return np.stack([channel(r, warmth + tint * .3), channel(g, -tint), channel(b, -warmth)], axis=1)


def white_balance(linear, usable=None):
    """Linear R/G and B/G on the grey patches, in percent off neutral."""
    patches = [i for i in WB_PATCHES if usable is None or usable[i]]
    grey = linear[patches]
    return np.array([(grey[:, 0] / grey[:, 1]).mean() - 1, (grey[:, 2] / grey[:, 1]).mean() - 1]) * 100


def saturation_of(linear, reference, usable=None):
    """Chroma of the 18 colour patches after white balance with lightness divided out, against the reference."""
    patches = [i for i in WB_PATCHES if usable is None or usable[i]]
    gains = np.array([1 / (linear[patches, 0] / linear[patches, 1]).mean(), 1.0, 1 / (linear[patches, 2] / linear[patches, 1]).mean()])
    balanced, wanted = lab_of_linear(linear * gains)[:18], lab(reference)[:18]
    ratio = np.hypot(balanced[:, 1], balanced[:, 2]) / (balanced[:, 0] + 16)
    return float(np.mean(ratio / (np.hypot(wanted[:, 1], wanted[:, 2]) / (wanted[:, 0] + 16))))


def style(tone, balance=(0.0, 0.0), saturation=1.0):
    gamma, contrast, fade, highlight = tone
    return np.array([balance[0], balance[1], saturation, contrast, fade, highlight, gamma])


class Charts:
    def __init__(self, path):
        data = json.loads(Path(path).read_text())
        self.reference = np.array(data['reference_srgb'], float) / 255
        self.phones = {}
        for key, phone in data['phones'].items():
            raw = np.array(phone['patches'], float)
            # Colours outside sRGB (Display P3 files) stay as they are for the measurements and are clipped for rendering.
            linear = (decode(raw / 255) @ TO_XYZ[phone['space']].T) @ np.linalg.inv(TO_XYZ['srgb']).T
            wide = np.sign(linear) * encode(np.abs(linear)) * 255
            usable = ~((raw >= 251).any(axis=1) | (wide >= 251).any(axis=1))      # a clipped patch has lost its colour
            srgb = np.clip(wide, 0, 255) / 255
            self.phones[key] = dict(group=phone['group'], srgb=srgb, usable=usable, grey=srgb[GREYS] @ LUMA,
                                    wb=white_balance(linear, usable), saturation=saturation_of(linear, self.reference, usable))
        galaxies = [p for p in self.phones.values() if p['group'] == 'galaxy']
        self.base = np.clip(encode(np.mean([decode(p['srgb']) for p in galaxies], axis=0)), 0, 1)
        self.base_wb = white_balance(decode(self.base))
        self.base_saturation = saturation_of(decode(self.base), self.reference)
        self.galaxy_saturation = float(np.mean([p['saturation'] for p in galaxies]))
        self.base_grey = self.base[GREYS] @ LUMA
        relative = {k: self.phones[k]['wb'] - self.base_wb for k in IPHONES}
        self.typical_wb = np.mean([relative[k] for k in TYPICAL_WB], axis=0)
        self.wanted_wb = dict(relative)
        self.wanted_wb['13'] = (relative['13'] + self.typical_wb) / 2
        self.wanted_wb['15pro'] = self.typical_wb
        measured = [k for k in IPHONES if k not in TONE_FROM]
        self.family_lightness = np.mean([lightness(self.phones[k]['grey']) for k in measured], axis=0)

    def derive(self, key, shared, gain=1.0):
        sources = [self.phones[s] for s in TONE_FROM.get(key, [key])]
        wanted_grey = lightness(np.mean([s['grey'] for s in sources], axis=0))
        clipped = np.array([any(not s['usable'][g] for s in sources) for g in GREYS])
        start = lightness(self.base_grey)
        goal = start + gain * (shared * (self.family_lightness - start) + (wanted_grey - self.family_lightness))
        greys = np.repeat(self.base_grey[:, None], 3, axis=1)

        def tone_error(tone):
            error = lightness(grade(greys, style(tone)) @ LUMA) - goal
            return np.where(clipped, np.minimum(error, 0), error)      # a clipped patch only says "at least this bright"

        tone = least_squares(tone_error, [1.0, 1.0, 0.01, 0.0], bounds=(TONE_LOW, TONE_HIGH)).x
        wanted_wb = self.wanted_wb[key] * gain
        balance = least_squares(
            lambda v: white_balance(decode(grade(self.base, style(tone, v)))) - self.base_wb - wanted_wb, [0.0, 0.0]).x
        sources = [self.phones[s] for s in SATURATION_FROM.get(key, [key])]
        wanted_saturation = 1 + gain * (float(np.mean([s['saturation'] for s in sources])) / self.galaxy_saturation - 1)
        saturation = brentq(lambda s: saturation_of(decode(grade(self.base, style(tone, balance, s))), self.reference)
                            - self.base_saturation * wanted_saturation, 0.4, 3.0)
        values = style(tone, balance, saturation)
        change = float(np.linalg.norm(lab(grade(self.base, values)) - lab(self.base), axis=1).mean())
        mid = float((grade(np.full((1, 3), .5), style(tone)) @ LUMA)[0])
        return values, dict(wb=wanted_wb, saturation=wanted_saturation, change=change, mid=mid)


def option(name, default):
    return type(default)(sys.argv[sys.argv.index(name) + 1]) if name in sys.argv else default


def main():
    charts = Charts(Path(__file__).resolve().parent / 'colorchecker.json')
    shared, target = option('--shared', 0.5), option('--target', 12.5)
    print(f'Galaxy baseline: mean of {sum(p["group"] == "galaxy" for p in charts.phones.values())} charts, '
          f'grey R/G {charts.base_wb[0]:+.1f}% B/G {charts.base_wb[1]:+.1f}%')
    print(f'typical iPhone grey balance against it: R/G {charts.typical_wb[0]:+.1f} B/G {charts.typical_wb[1]:+.1f} points')
    print(f'{"style":6s} {"gain":>5s}  {"R/G":>6s} {"B/G":>6s}  {"satur.":>6s}  {"mid grey":>8s}  {"change":>6s}  '
          + ' '.join(f'{n[:9]:>9s}' for n in NAMES))
    result, table = {}, []
    for key in IPHONES:
        values, info = charts.derive(key, shared)
        gain, measured = 1.0, info
        if target > 0:
            gain = float(np.clip(np.sqrt(target / info['change']), 0.85, 1.6))
            values, info = charts.derive(key, shared, gain)
        result[key] = {n: round(float(v), 3) for n, v in zip(NAMES, values)}
        table.append(f'| {key} | {measured["wb"][0]:+.1f} | {measured["wb"][1]:+.1f} | {measured["saturation"]:.2f} | {gain:.2f} | '
                     + ' | '.join(f'{result[key][n]:.3f}' for n in NAMES) + f' | {info["mid"]:.2f} |')
        print(f'{key:6s} {gain:5.2f}  {info["wb"][0]:+6.1f} {info["wb"][1]:+6.1f}  {info["saturation"]:6.3f}  '
              f'{.5:.2f}>{info["mid"]:.3f}  {info["change"]:6.2f}  ' + ' '.join(f'{result[key][n]:9.3f}' for n in NAMES))
    print('\nLook.kt values (warmth, tint, saturation, contrast, fade, highlight, gamma):')
    for key in IPHONES:
        print(f'  {key:6s} ' + ', '.join(f'{result[key][n]:.3f}f'.replace('0.', '.', 1) if abs(result[key][n]) < 1
                                         else f'{result[key][n]:.3f}f' for n in NAMES))
    if '--markdown' in sys.argv:      # the table in docs/COLOR_EVIDENCE.md
        print('\n| style | R/G | B/G | saturation ratio | gain | ' + ' | '.join(NAMES) + ' | mid grey |')
        print('|' + ' --- |' * (len(NAMES) + 6))
        print('\n'.join(table))
    if '--json' in sys.argv:
        Path(option('--json', '')).write_text(json.dumps(result, indent=1) + '\n')


if __name__ == '__main__':
    main()
