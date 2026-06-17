# Çağrı Takip — overlay arayan kimliği uygulaması

Çağrı gelince numarayı alır, `cagri-ekrani.html`'i çalan ekranın üstüne overlay
olarak basar. Müşteriyse isim/geçmiş, yeni numaraysa not ekranı. APK'yı GitHub
bulutta derler; bilgisayarına hiçbir şey kurmazsın.

---

## ÖNKOŞUL (bir kez)

1. `cagri-ekrani.html` dosyasını sunucuya yükle: `…/public_html/cagri-ekrani/index.html`
2. İçindeki `SUPABASE_ANON_KEY`'i yapıştır.
3. Supabase SQL Editor'de `cagri_notlari` tablosunu oluştur (daha önce verilen SQL).
4. `app/src/main/java/com/pestizmir/cagri/Config.java` içindeki `BASE_URL`'in
   doğru olduğundan emin ol (varsayılan: `https://pestizmir.com.tr/cagri-ekrani/`).

---

## APK'yı üret (GitHub)

1. GitHub'da **yeni bir repo** aç (private olabilir).
2. Bu klasörün **içindeki tüm dosyaları** repoya yükle.
   `settings.gradle` reponun **kökünde** olmalı (alt klasörde değil).
   - Kolay yol: GitHub repo sayfası > "Add file" > "Upload files" > bu klasörün
     içindekileri sürükle-bırak. (veya `git add . && git commit && git push`)
3. Repo > **Actions** sekmesine geç. "Build APK" otomatik çalışır (~3-5 dk).
4. Yeşil tik gelince çalışmaya tıkla > en altta **Artifacts** > **cagri-takip-apk** indir.
5. İnen zip'in içinden `app-debug.apk` çıkar.

> BASE_URL'i değiştirirsen yeni bir commit/push at → yeni APK üretilir.

---

## Telefona kur ve izinleri ver

1. `app-debug.apk`'yı telefona aktar (Drive / WhatsApp / USB), dosyaya dokun,
   "bilinmeyen kaynaktan yüklemeye izin ver" deyip kur.
2. **Çağrı Takip** uygulamasını aç, sırayla:
   - **1) Diğer uygulamaların üzerinde göster** → izni aç.
     - Android 15'te toggle gri/kapalıysa: Ayarlar > Uygulamalar > Çağrı Takip >
       sağ üst **⋮** > **"Kısıtlı ayarlara izin ver"** > PIN ile onayla > tekrar dene.
   - **2) Arayan kimliği uygulaması yap** → çıkan ekranda Çağrı Takip'i seç.
   - **3) Pil: sınırsız yap** → Pil > **Sınırsız**.
3. Başka bir hattan kendini ara → kart çalan ekranın üstünde çıkmalı.

---

## Notlar

- APK debug-imzalıdır; kişisel kullanım için yeterli, Play Store'a gerek yok.
- Telefon uygulamanı değiştirmez; sadece "arayan kimliği" yardımcısı olarak çalışır.
- Not yazmak: kart çıkınca üstüne dokunup yazabilirsin. Klavye overlay'de
  açılmazsa, uygulamayı normal aç (simgesinden) — not ekranı orada da var, son
  numarayı arayıp not eklersin.
- Bu v1; cihazında ilk denemede takılırsa muhtemel yerler: overlay izni (kısıtlı
  ayarlar), boş/blank kart (BASE_URL yanlış veya sayfa yüklenmemiş). Hangi adımda
  ne gördüğünü yaz, ince ayar yaparız.
