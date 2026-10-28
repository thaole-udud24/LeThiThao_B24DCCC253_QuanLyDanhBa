# Quản lý danh bạ liên hệ (Android · Java + Kotlin)

Ứng dụng 4 màn hình: **Login → Main (danh sách) → Add → Detail**, dữ liệu lưu trong bộ nhớ (`ArrayList<Contact>`), giao diện pastel theo ảnh tham khảo.

- **Tài khoản demo:** `admin` / `123456`
- Chi tiết tính năng: xem [TINH_NANG.md](TINH_NANG.md)

## Kiến trúc (Java ↔ Kotlin cùng một module `app`)

```
com.example.contacts
├─ ui/        (Java)   login · main · add · detail + BaseActivity (edge-to-edge)
├─ domain/    (Kotlin) model (Contact, ContactGroup, Gender) · validator · query (lọc/sắp xếp)
├─ data/      (Kotlin) ContactRepository · InMemoryContactRepository · ServiceLocator
├─ auth/      (Kotlin) AuthService · FixedAuthService
├─ viewmodel/ (Kotlin) MainViewModel
└─ util/      (Kotlin) Constants · Extensions · AppIcons · AvatarPalette
```

- Activity (Java) chỉ gọi interface `ContactRepository`, `AuthService`; validate nằm ở `ContactValidator`.
- Thay InMemory bằng Room / FixedAuth bằng API: chỉ sửa `ServiceLocator`.
- **Icon:** lấy từ thư viện [Iconics](https://github.com/mikepenz/Android-Iconics) + Google Material typeface, gom ở `AppIcon`/`AppIcons`. Không dùng icon drawable có sẵn. Đổi bộ icon = sửa 1 file.
- Công nghệ: ViewBinding, Material 3, ViewModel + LiveData, ListAdapter/DiffUtil, Activity Result API, Parcelize, edge-to-edge (`WindowInsetsCompat`).

## Build

**Android Studio:** mở thư mục dự án → Sync → Run. (Android Studio tự tạo Gradle wrapper theo `gradle-wrapper.properties`.)

**Docker** (không cần cài Android Studio):

```bash
docker compose up --build builder     # APK ở ./output/app-debug.apk
docker compose --profile test run --rm test
adb install -r output/app-debug.apk
```

Docker chỉ build APK / chạy test / lint, không chạy được giao diện.

## Responsive

| Cơ chế | File |
|---|---|
| Kích thước theo màn hình (sw360/sw600/sw840) | `values*/dimens.xml` |
| Số cột danh sách: 1 (điện thoại dọc) · 2 (xoay ngang, tablet) | `values*/integers.xml` |
| Giới hạn bề ngang màn danh sách (640 / 720 / 840dp), căn giữa | `main_max_width` trong `values*/dimens.xml`, `activity_main.xml` |
| Form giới hạn bề ngang + căn giữa | `layout_constraintWidth_max="@dimen/content_max_width"` |
| Cuộn khi màn thấp / landscape / chữ lớn | `NestedScrollView` + header `AppBarLayout` |
| Status bar / nav bar / notch / bàn phím | `applySystemBarsPadding` (`Extensions.kt`) |
| Xoay màn hình | `MainViewModel` + repository singleton; view state của form Add tự khôi phục |

Đã chạy thử trên emulator (đổi bằng `adb shell wm size/density`): 360x640dp, 411x914dp, tablet 914x1463dp, landscape điện thoại 640x360dp, font hệ thống 200%. Chưa thử: 600x960dp, 1280x800dp, gập (foldable).

## Mở rộng

- Thêm tính năng = thêm package mới (vd. `export/`), không sửa code cũ.
- Thêm nhóm liên hệ: thêm 1 dòng trong `ContactGroup` + chuỗi trong `strings.xml`.
- Lưu bền vững: viết `RoomContactRepository : ContactRepository`, đổi trong `ServiceLocator`.

## Test

`ContactValidatorTest`, `InMemoryContactRepositoryTest`, `FixedAuthServiceTest` (Kotlin) và `KotlinInteropJavaTest` (Java gọi Kotlin): `gradle testDebugUnitTest`.
