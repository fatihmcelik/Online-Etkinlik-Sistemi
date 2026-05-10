


# Online Etkinlik ve Organizasyon Yönetim Sistemi

Bu proje, kullanıcıların çeşitli kategorilerdeki etkinlikleri keşfedebildiği, filtreleyebildiği, güvenli bir şekilde bilet satın alabildiği ve etkinlikleri değerlendirebildiği kapsamlı bir **Spring Boot** tabanlı web uygulamasıdır. Sistem aynı zamanda yöneticiler (Admin) için detaylı bir yönetim paneli, bilet/kota takibi ve gelişmiş sistem loglama özellikleri sunar.

##  Öne Çıkan Özellikler

###  Kullanıcı Özellikleri

 **Gelişmiş Etkinlik Keşfi:** Etkinlikleri kategoriye, mekana, tarihe, maksimum bilet fiyatına ve kelimeye göre (arama çubuğu) filtreleme.
 **Güvenli Bilet Satın Alma:** İyzico ödeme altyapısı entegrasyonu ile kredi kartı üzerinden güvenli bilet (E-Bilet) alımı ve PNR kodu üretimi.
 **Kullanıcı Etkileşimi:** Etkinlikleri favorilere ekleme, geçmiş etkinliklere 1-5 arası yıldız verme ve yorum yapma.
 **Profil ve Bilet Yönetimi:** Kullanıcı profilini güncelleme, satın alınan biletleri, favorileri ve yapılan yorumları tek bir ekrandan yönetme.
 **Güvenli Kimlik Doğrulama:** Şifremi unuttum akışı (SHA-256 ile hashlenmiş güvenli tokenlar ile), Brute-Force koruması (5 hatalı girişte hesap kilitleme).

###  Admin (Yönetici) Özellikleri

 **Kapsamlı Dashboard:** Toplam kullanıcı, etkinlik, bilet ve gelir istatistiklerini görüntüleme.
 **Etkinlik ve Kategori Yönetimi:** Yeni etkinlik, kategori, konuşmacı ve mekan ekleme, düzenleme, silme ve aktif/pasif durumlarını değiştirme.
 **Bilet Tipi ve Kota Yönetimi:** Bir etkinliğe birden fazla bilet tipi (Örn: VIP, Standart) ekleme, fiyatlandırma ve kapasite (kota) belirleme. Satış yapılmış bilet tiplerinin silinmesini engelleyen veri bütünlüğü koruması.
 **Kullanıcı Yönetimi:** Sistemdeki kullanıcıları listeleme, hesapları kilitleme/açma (ban yönetimi).
 **Sistem Logları (Audit):** Kullanıcı girişleri, hatalı şifre denemeleri, ödeme hataları, bilet alımları ve entity değişiklikleri gibi tüm kritik işlemlerin IP ve User-Agent bazlı loglanması.

##  Kullanılan Teknolojiler

 **Backend:** Java, Spring Boot, Spring MVC, Spring Data JPA
 **Güvenlik:** Spring Security, BCrypt Password Encoder
 **Veritabanı:** MySQL (Relational Database Management)
 **Ödeme Entegrasyonu:** Iyzico API
 **Mimari Yaklaşımlar:** DTO (Data Transfer Object) Pattern, Global Exception Handling, Event Listeners (Login Success/Failure)
 **Frontend / View:** Thymeleaf, HTML/CSS/JS (Statik dosyalar)

##  Proje Yapısı ve Veritabanı Mimarisi

Proje, birbirleriyle ilişkili (OneToMany, ManyToMany) karmaşık bir veritabanı yapısı kullanır:

`User`, `Role`, `PasswordResetToken` (Güvenlik ve Kimlik)
 `Event`, `Category`, `Location`, `Speaker` (Etkinlik Çekirdeği)
 `Ticket`, `TicketType` (Biletleme ve Kota)
 `Payment`, `PaymentItem` (İyzico Ödeme Kayıtları)
 `Favorite`, `Review` (Kullanıcı Etkileşimi)
 `SystemLog` (Sistem İzlenebilirliği)

## Kurulum ve Çalıştırma

Projeyi yerel ortamında çalıştırmak için aşağıdaki adımları izleyebilirsin.

**1. Depoyu Klonlayın**

```bash
git clone https://github.com/kullaniciadin/online-etkinlik-sistemi.git
cd online-etkinlik-sistemi

```

**2. Veritabanı Ayarlarını Yapılandırın**
`src/main/resources/application.properties` dosyasını açın ve MySQL veritabanı ile İyzico API bilgilerinizi girin:

```properties
# Veritabanı Ayarları
spring.datasource.url=jdbc:mysql://localhost:3306/online_etkinlik_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=sifreniz
spring.jpa.hibernate.ddl-auto=update

# Iyzico API Ayarları (Test Ortamı Sandbox)
iyzico.api.key=SENIN_API_ANAHTARIN
iyzico.secret.key=SENIN_GIZLI_ANAHTARIN
iyzico.base.url=https://sandbox-api.iyzipay.com

```

**3. Projeyi Derleyin ve Başlatın**

```bash
mvn clean install
mvn spring-boot:run

```

Proje varsayılan olarak `http://localhost:8080` adresinde çalışacaktır. Sistem ilk ayağa kalktığında `DataInitializer` sınıfı sayesinde `ROLE_USER` ve `ROLE_ADMIN` rolleri veritabanına otomatik olarak eklenecektir.

##  Rol ve Yetkilendirme (Security)

 `/` (Ana sayfa, etkinlik listesi, kayıt/giriş): Herkese açık (PermitAll).
 `/profile`, `/payments/`, `/tickets`: Sadece giriş yapmış kullanıcılar (Authenticated).
 `/admin/`: Sadece `ROLE_ADMIN` yetkisine sahip kullanıcılar erişebilir.

##  Hata Yönetimi (Exception Handling)

Projede Controller seviyesinde patlayan hataları (örn: veritabanı kısıtlamaları, null pointer) yakalamak ve kullanıcıyı şık bir hata mesajıyla uygun sayfaya (`Referer` header'ı üzerinden) geri yönlendirmek için `@ControllerAdvice` tabanlı bir `GlobalExceptionHandler` kullanılmıştır.
