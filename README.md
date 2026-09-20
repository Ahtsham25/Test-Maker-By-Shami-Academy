# Paper Generator — Shami Academy

پنجاب بورڈ کے مطابق نہم اور دہم کے لیے کسٹم ایگزام پیپر جنریٹر۔

## 1. ڈیٹا ریپو سیٹ اپ (سب سے پہلا کام)

`sample_data/` فولڈر کو ایک الگ GitHub ریپو میں اپلوڈ کریں (مثلاً `papergenerator-data`)، پھر
`DataRepository.kt` میں `BASE_URL` کو اپنے ریپو کے raw URL سے بدل دیں:

```
https://raw.githubusercontent.com/Ahtsham25/papergenerator-data/main/
```

### فولڈر اسٹرکچر:
```
index.json                         ← classes کی لسٹ
class_9/subjects.json              ← نہم کے تمام مضامین
class_9/physics/chapters.json      ← فزکس کے ابواب
class_9/physics/ch1/mcq.json       ← باب 1 کے MCQs
class_9/physics/ch1/short.json     ← باب 1 کے مختصر سوالات
class_9/physics/ch1/long.json      ← باب 1 کے تفصیلی سوالات
... (class_10 کے لیے بھی یہی پیٹرن، + mutalya_pakistan)
```

**فی الحال مکمل سیمپل ڈیٹا صرف یہاں موجود ہے:**
- `class_9/physics/ch1` (3 MCQs, 2 Short, 1 Long)
- `class_10/physics/ch1` (2 MCQs, 1 Short, 1 Long)

باقی تمام مضامین/ابواب کی فائلیں خالی (`"questions": []`) بنی ہوئی ہیں — بس آپ کو اسی JSON فارمیٹ میں
سوالات بھرنے ہیں۔ نیا چیپٹر شامل کرنے کے لیے بس اسی پیٹرن میں فولڈر بنائیں اور `chapters.json` میں اندراج کریں۔

## 2. فونٹ

`app/src/main/assets/fonts/` میں `NotoNastaliqUrdu-Regular.ttf` رکھیں
(مفت ڈاؤنلوڈ: https://fonts.google.com/noto/specimen/Noto+Nastaliq+Urdu)

## 3. AdMob

`AndroidManifest.xml` میں App ID اور `RewardedAdManager.kt` میں Rewarded Ad Unit ID کو اپنے اصل
AdMob IDs سے بدل دیں (ابھی ٹیسٹ IDs لگے ہیں)۔

## 4. GitHub Actions سے بلڈ

`.github/workflows/build.yml` پہلے سے تیار ہے (آپ کے موجودہ پیٹرن کی طرح)۔ ریپو کے
Settings → Secrets میں یہ چار secrets ڈالیں:
- `KEYSTORE_BASE64` (اپنی .jks فائل کو `base64 keystore.jks` کر کے پیسٹ کریں)
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`

پھر Actions ٹیب سے "Build Release AAB" ورک فلو رن کریں — AAB اور APK دونوں artifact میں ملیں گے۔

## 5. ایپ کا فلو

Class 9/10 → Subject (پہلی کتاب فری، باقی رشوارڈڈ ایڈ سے اَن لاک) → Chapter (پہلا باب فری، باقی
رشوارڈڈ ایڈ سے اَن لاک) → Questions (بورڈ پیٹرن خودکار یا مینول سلیکشن) → Preview (ادارے کا نام،
فونٹ سائز، اردو/انگلش/بائی لینگویج) → Print/Save as PDF (سسٹم پرنٹ ڈائیلاگ، "Save as PDF" منتخب
کر کے ڈیوائس پر محفوظ ہو جاتا ہے)۔

## 6. اَن لاک منطق (`UnlockManager.kt`)

- ہر کلاس کی پہلی کتاب ہمیشہ فری
- ہر کتاب کا پہلا باب ہمیشہ فری
- باقی سب رشوارڈڈ ایڈ دیکھ کر اَن لاک ہوتے ہیں، اور مستقل (SharedPreferences) یاد رہتا ہے
