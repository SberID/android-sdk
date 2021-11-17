
## Android SDK

### Общие сведения

Android SDK помогает реализовать получение кода авторизации (Auth Code) Сбер ID минимальными усилиями со стороны разработчика и в соответствии с утвержденными гайдами по отображению кнопки. Чтобы добавить поддержку Сбер ID в свое приложение, следуйте инструкциям ниже.

### Подключение

Добавьте файл .aar в папку libs вашего проекта: 

![Скриншоти aar](images/Android1.png ':size=600')

Перейдите в build.gradle вашего модуля: 

![Скриншот gradle](images/Android2.png ':size=600')

И добавьте зависимость в раздел dependencies:

```java
dependencies {
	...
	implementation(name:'SberIdSDK', ext:'aar')
	...
}
```

!>SDK реализована на языке Kotlin (версия 1.4.0), поэтому если в вашем проекте используется только Java и никакие дополнительные зависимости на Kotlin ранее не подключались, нужно добавить поддержку Kotlin (версия указана для примера, актуальна на момент написания документации):

```java
dependencies {
	...
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.10")
	...
}
```

!> Android Studio покажет предупреждение о том, что версия Kotlin Plugin (null) не соответствует версии подключенной библиотеки, но в данном случае подключение к проекту Kotlin Plugin не является обязательным.
Все вызовы статических методов SDK в вашем Java-коде по примерам ниже необходимо выполнять через стандартный вспомогательный объект Companion, напрмер:

```java

SberIDLoginManager.Companion.SberIDBuilder sberIDBuilder = SberIDLoginManager.Companion.sberIDBuilder()



Перейдите в build.gradle вашего проекта:

![Скриншот gradle](images/Android3.png ':size=600')

Добавьте зависимость:

```java
allprojects {
    repositories {
        flatDir {
            dirs 'libs'
        }
    }
}
```

Перейдите в settings.gradle вашего проекта:

![Скриншот gradle 2](images/Android4.png ':size=600')

И подключите библиотеку к вашему проекту:

```java
include ':SberIdSDK'
```

### Поддержка Android 11+


!> Начиная с версии 1.3.1 SDK поддерживает определение видимости МП СБОЛ по требованиям, введенным в Андроид 11. Для возможности корректной сборки проекта необходимо, чтобы версия Android Gradle Plugin в вашем приложении была соответствующей. Подробнее про функционал видимости внешних пакетов можно ознакомиться в документации Google:

https://developer.android.com/about/versions/11/privacy/package-visibility 

https://developer.android.com/studio/releases/gradle-plugin?hl=ru

Если версия Android Gradle Plugin будет ниже требуемой, при сборке проекте будет возникать ошибка Android resource linking failed. Для поддержки работоспособности достаточно минорного обновления версии Android Gradle Plugin в зависимости от той, которая у вас используется сейчас, подробнее про версии плагина можно прочитать здесь:

https://android-developers.googleblog.com/2020/07/preparing-your-build-for-package-visibility-in-android-11.html

Минимально допустимая версия Android Gradle Plugin, поддерживающая требования в Андроид 11 - 3.3.3. Минимально допустимая версия Gradle, поддерживающая эту версию плагина - 4.10.1.


### Отправка аналитических событий

Начиная с версии 1.3.0 SDK автоматически формирует и отправляет на сервер Сбербанка события, связанные с авторизацией по Сбер ID (показ и клик по кнопке, результат авторизации).

?> при установке ширины кнопки менее минимально допустимой величины и невозможности автоматически установить корректное значение дополнительно к событию показа будет отправлено событие установки некорректной ширины. Как и ранее, информацию об этом также можно увидеть в логах, см. раздел "Добавление кнопки".


### Отправка аналитических событий

Начиная с версии 1.3.0 SDK автоматически формирует и отправляет на сервер Сбербанка события, связанные с авторизацией по Сбер ID (показ и клик по кнопке, результат авторизации).

Для корректной работы функционала отправки событий необходимо выполнить следующие действия:
- если ваше приложение использует только Kotlin, то добавьте в файл build.graddle вашего приложения в раздел android блок кода для совместимости с Java (если уже используете Java, это делать не нужно):

![Скриншот gradle 3](images/Android2.png ':size=600')


```java
android {
	...
	compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```

- добавьте зависимости в раздел dependencies:

```java
dependencies {
	...
	//region Dependencies for support analytic events in SberIdSDK
    implementation("com.squareup.okhttp3:okhttp:4.8.1") // min 4.6.0
    implementation("androidx.annotation:annotation:1.1.0")
    implementation("androidx.core:core:1.3.2") // min 1.3.0
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.10.4")
    implementation("com.fasterxml.jackson.core:jackson-core:2.10.4")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.4")
    //end region
}
```

?> 
- если вы уже используете в своем приложении какие-то из указанных выше зависимостей, указывать их заново не нужно
- версии добавляемых библиотек рекомендуется использовать не ниже тех, которые указаны в примере выше в явном виде или комментарии //min

Синхронизируйте ваш проект – библиотека подключена!


### Добавление кнопки

Для добавления кнопки используйте в своей разметке вью **sberid.sdk.auth.view.SberIDButton**


```java
<sberid.sdk.auth.view.SberIDButton
    android:layout_width="match_parent"
    android:layout_height="wrap_content"/>
```

Кнопка автоматически устанавливает иконку Сбербанка, текст, шрифт, цвет по указанным в атрибутах значениям либо значениями по умолчанию.
Высота кнопки органичена интервалом 28dp-64dp, при установке высоты, выходящей за пределы интервала, высота будет автоматически приведена в соответствующей границе.
Ширина кнопки имеет минимальное значение, исходя из размеров выбранного текста, логотипа и отступов.
Максимальная ширина кнопки не ограничена.

!> 
- при установке ширины кнопки менее минимально допустимой величины будет выполнена попытка приведения ширины кнопки к минимально допустимой. При этом в логи будет выведена ошибка с указанием расчетного и фактического значений в dp, просьба обращать на это внимание. Найти ошибку в логах можно по тэгу SberIDButtonWidthError

Вам остается только добавить:
- параметры для положения кнопки
- отступы

Для большей информации о стиле кнопок, смотрите гайд по дизайну Сбер ID.


?> Пример (значение и способ размещения кнопки может отличаться, зависит от вашей реализации)

```java
<sberid.sdk.auth.view.SberIDButton
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="36dp"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintBottom_toBottomOf="parent"/>
```

### Кастомизация кнопки 

**Тип**

По умолчанию используется стандартный вид кнопки:

![Скриншот кнопки](images/screenone.png ':size=600')

Для изменения стиля кнопки, укажите параметр в xml кнопки app:buttonType="значение из списка".

![Скриншот кода](images/screentwo.png ':size=600')

Значение **'white_type** соответствует белой кнопке с серой обводкой:

![Скриншот кода](images/screenthree.png ':size=600')

### Обводка

В случае установки типа кнопки "white_type" есть возможность переопределить значение цвета обводки для соответствия вашему дизайну, если на экране присутствует несколько однотипных  кнопок.

Для этого установите конкретный цвет через атрибут кнопки app:buttonStrokeColor="@color/..."

При установке цвета обводки для стандартной зеленой кнопки значение будет проигнорировано.

![Скриншот кнопки 2](images/screeneight.png ':size=600')


### Скругление

Для изменения скругления кнопки, укажите параметр в xml кнопки app:buttonCornerRadius="значение_dp". Предопределённые значения "corner_small" и "corner_large" соответствуют 4 и 32 dp соответственно. Есть возможность указать здесь собственное значение радиуса скругления в dp.

![Скриншот кнопки 3](images/screennew.png ':size=600')

![Скриншот кнопки 4](images/screenfive.png ':size=600')

### Текст

Для изменения текста кнопки, укажите параметр в xml кнопки app:buttonText="значение из списка".

![Скриншот кода 2](images/screensix.png ':size=600')

Соответствие значений:

```java
login → Войти по Сбер ID
 
login_short → Сбер ID
 
continue_on → Продолжить по Сбер ID
 
fill → Заполнить по Сбер ID
```

**Значение по умолчанию** - Войти по Сбер ID. Установка собственного текста, шрифта, цвета текста не поддерживается.

!>
- allCaps всегда выключен (т.е. текст не будет заглавными буквами);
- тексты поддерживают английскую локализацию на устройстве. При выборе локализации, отличной от русской и английской, тексты будут на русском.

![Скриншот кнопки 5](images/screenseven.png ':size=600')

- В примере кнопка с текстом "Сбер ID" отрисована с минимально допустимой шириной 

### Персонализация кнопки

Начиная с версии SDK 1.2.0 кнопка входа по Сбер ID автоматически поддерживает персонализацию при выполнении следующих условий:
- на устройстве установлено МП СБОЛ версии от 11.8 и выше
- в МП СБОЛ пользователем включен функционал персонализации входа в приложениях партнера (МП СБОЛ - Профиль пользователя - Сбер ID - Настройки входа)

Дополнительных действий от разработчиков при интеграции этой версии SDK не требуется. 

Персонализация заключается в анимированном автоматическом изменении текста в кнопке на содержащий информацию об имени и фамилии пользователя. Все остальные параметры, которые вы указали при добавлении кнопки, остаются без изменений.

Если персонализация на стороне МП СБОЛ отключена либо на устройстве не установлено МП СБОЛ либо процедура персонализации завершилась неуспешно, текст на кнопке будет отображаться тот, который вы указали в разметке при ее добавлении.
- Если кнопка была добавлена с коротким текстом "Сбер ID", персонализация кнопки не будет запущена в любом случае. Если дизайн экрана предполагает наличие широкой кнопки входа по Сбер ID, рекомендуется использовать один из длинных вариантов текста

При запуске процедуры персонализации на кнопке отображается индикатор загрузки:

![Скриншот кнопки 6](images/screennine.png ':size=600')

![Скриншот кнопки 7](images/screenten.png ':size=600')

После завершения персонализации кнопка выглядит следующим образом:

![Скриншот кнопки 8](images/screeneleven.png ':size=600')

![Скриншот кнопки 9](images/screentwelve.png ':size=600')

Если имя пользователя слишком длинное, чтобы уместиться в отведенное размерами кнопки место, итоговый текст будет автоматически обрезан с правой стороны:

![Скриншот кнопки 9](images/screentheertin.png ':size=600')


### Управление статусом загрузки

Начиная с версии SDK 1.3.5 появилась возможность вручную программно управлять статусом загрузки в кнопке (анимированное лого СберБанка, как в разделе Персонализация кнопки).

Это может быть полезно, если перед тем, как дать возможность пользователю нажать на кнопку, вам необходимо например сделать запрос на сервер и получить oidc-параметры для авторизации по Сбер ID. Либо уже после запуска авторизации дождаться результата ее выполнения. При ручной установке статуса загрузки кнопка перестает реагировать на нажатия, при снятии - начинает. 

Установка/снятие статуса загрузки вручную не мешает работе процедуры персонализации кнопки, эти процессы не связаны друг с другом. Если на момент снятия вами статуса загрузки процедура персонализации еще не будет завершена, то анимация лого продолжится до ее завершения.

Предусмотрено два варианта управления статусом:

- атрибут в xml разметке кнопки app:buttonLoader="значение_true_false". Если ваш экран подразумевает ожидание каких-либо данных при первоначальной отрисовке UI, рекомендуем пользоваться именно этим способом, выставляя в разметке значение true. Если атрибут в разметке не указан, по умолчанию значение принимается за false.

![Скриншот кода 3](images/screenshot_load_manage.png ':size=600')

- публичный метод в классе кнопки SberIDButton для управления статусом загрузки по мере необходимости из программного кода

```java
/**
* Установить актуальный статус индикатора загрузки в кнопке
* В слуачае установки статуса загрузки стартует анимация, текст в кнопке зануляется
* В случае отмены статуса загрузки анимация останавливается, в кнопке отображается
* стандартный/персонализированный текст, либо продолжается анимация до завершения работы процедуры персонализации
*
* @param isLoading - true, если необходимо отобразить на кнопке статус загрузки, false - в противном случае
*/
fun setLoaderState(isLoading: Boolean)
```

### Запуск авторизации по Сбер ID

Класс **SberIDLoginManager** содержит все методы для работы с авторизацией по Сбер ID.

!>
- для корректной работы необходимо обеспечить, чтобы экземпляр созданного класса **SberIDLoginManager** создавался и переживал весь цикл авторизации по Сбер ID (запрос на авторизацию и проверка результата). Будьте внимательны при работе с вашим DI, в противном случае при проверке результате вы будете получать ошибку "invalid_state"

Для запуска аутентификации используйте метод loginWithSberbankID.

Класс **SberIDLoginManager** содержит статический билдер для удобного создания uri для авторизации по Сбер ID, он позволяет минимизировать ошибки ввода значения в неверный параметр.
Класс **PkceUtils**  содержит утилиты для создания значений параметров протокола PKCE (необязательные параметры, если вы не используете PKCE).

```java
// указано для примера, инстанцирование класса SberIDLoginManager выполняйте, как удобно, с обеспечением требования, указанного выше
private val sberIDLoginManager = SberIDLoginManager()

//Создание параметров для поддержки протокола PKCE. 
val codeVerifier = PkceUtils.generateRandomCodeVerifier(SecureRandom())
val codeChallenge = PkceUtils.deriveCodeVerifierChallenge(codeVerifier)


//Создание Uri с параметрами для аутентификации, все значения нужно поменять на свои, тут указаны примеры
val uri = SberIDLoginManager
	.sberIDBuilder()
    .clientID("512e1152-e14c-5223-a125-d519eb68bb86")
    .scope("openid name email mobile birthdate gender")
    .state("ffad1d59c1e34844a1415226103d44f3")
    .nonce("b1947d4f10a24eb0a21155239be9b066")
    .redirectUri("app://merchant_app/")
    .codeChallenge(codeChallenge) //Необязательный параметр
    .codeChallengeMethod(PkceUtils.getCodeChallengeMethod()) //Необязательный параметр
    .build()


//Запуск аутентификации по SberbankID, первым параметром нужно передать контекст
sberIDLoginManager.loginWithSberbankID(this, uri)
```

Необходимо направить запрос на support@ecom.sberbank.ru на добавление deeplink в список доверенных. В запросе указывается client_id и список deeplink, по которым будет производиться возврат в мобильное приложение партнера. Сотрудник банка добавит домен в список разрешенных.

После выполнения авторизации по Сбер ID в обратном интенте придет deeplink, содержащий результаты авторизации.

В манифесте вашего приложения для активити, которая будет обрабатывать результат авторизации по Сбер ID, необходимо указать схему и хост обратного deeplink (**они должны совпадать с redirectURI, который вы присылаете к нам**). Если хотите обрабатывать обратный deeplink в той же активити, в которой и стартовали аутентификацию, необходимо указать в атрибуте launchMode для вашей активити значение "singleTop" и получать интент с результатом в методе onNewIntent(intent: Intent).

Примеры обработки deeplink:

```java
<activity android:name=".MainActivity"
	//для обработки диплинка в той же активити
    android:launchMode="singleTop">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data
            android:host="merchant_app"
            android:scheme="app" />
    </intent-filter>
</activity>
```

Для проверки результата передать полученный intent в метод _SberIDLoginManager#getSberIDAuthResult(intent)_, который его обработает ответ и вернет сущность _SberIDResultModel_.

В большинстве случаев вам будет нужен nonce и authCode, они приходят в при успешной авторизации и вы их должны передать в свой бэк для дальнейшей аутентификации вместе с codeVerifier (если используете), созданным ранее.

Также будет передан параметр state, который был получен от сервера авторизации. Проверка на соответствие изначально переданному в запросе от партнера параметру state выполняется на стороне SDK, см. раздел Описание ошибок. 

```java
/**
 * Сущность ответа от SberID
 * @property isSuccess        true если ответ положительный и вход произведен удачно.
 * @property nonce            Значение, сгенерированное внешней АС для предотвращения атак
 *                            повторения.
 * @property state            Значение, сгенерированное внешней АС для предотвращения CSRF атак.
 * @property authCode         Код авторизации клиента.
 * @property errorDescription Текст ошибки, не показывайте клиентам, они только для разработчиков.
 *                            Типы ошибок:
 *                            - invalid_request: В запросе отсутствуют обязательные атрибуты;
 *
 *                            - unauthorized_client: АС-источник запроса не зарегистрирована в
 *                            банке, АС-источник запроса заблокирована в банке, или значение
 *                            атрибута client_id не соответствует формату;
 *
 *                            - unsupported_response_type: Значение атрибута response_type не равно
 *                            «code»;
 *
 *                            - invalid_scope: Значение атрибута scope не содержит параметр openid в
 *                            начальной позиции или запрошенный scope содержит значения, недоступные
 *                            для АС-источника запроса
 * @property errorCode        Приходит только в случае ошибки 5, когда вы неверно передали паоаметры,
 *                            ошибки описаны выше. В случае неожиданной ошибки, такие как падения
 *                           приложения и тп.
 * @author Gerasimenko Nikita
 */
data class SberIDResultModel @JvmOverloads constructor(
    var isSuccess: Boolean? = null,
	var state: String? = null,
    var nonce: String? = null,
    var authCode: String? = null,
    var errorDescription: String? = null,
    var errorCode: String? = null
)
```

Пример обработки в том же окне:

```java
//В методе onNewIntent, если примеить для активити android:launchMode="singleTop"
override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    val result = sberIDLoginManager.getSberIDAuthResult(intent)
}
```

При обработки в отдельном окне можно перехватить в методе _onCreate(savedInstanceState: Bundle?)_

```java
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val result = sberIDLoginManager.getSberIDAuthResult(intent)
}
```

### Поддержка бесшовной авторизации

Начиная с версии SDK 1.3.5 реализована поддержка бесшовной авторизации по Сбер ID, когда после перехода из приложений СберБанка в ваше приложение необходимо автоматически запустить авторизацию без показа кнопки и необходимости пользователю выполнять лишнее действие по нажатию на нее.

В диплинке, который придет в ваше приложение, помимо параметров, которые вы заложите в него самостоятельно по вашим требованиям, будет приходить дополнительный параметр, содержащий строку со схемой и хостом, которую нужно будет передать в билдер диплинка авторизации по Сбер ID (см. раздел Запуск авторизации по Сбер ID).

?> Для стандартной (не бесшовной) авторизации по Сбер ID по кнопке выполнять указанные в этом пункте действия не требуется 


Чтобы получить значение этого параметра, необходимо воспользоваться следующим методом в классе **SberIDLoginManager**, передав в него исходный uri, полученный при перехода в ваше приложение в сценарии бесшовной авторизации (uri находится в getIntent().getData()).


```java
/**
* Получить схему диплинка в формате app://host для авторизации по Сбер ID
* из исходного Uri при переходе в МП партнера в сценарии бесшовной авторизаии
* Полученное значение необходимо передать в метод [SberIDBuilder.uriScheme] при построении диплинка на авторизацию
*
* @param uri исходный Uri при переходе в МП партнера при бесшовной авторизации
*/
fun getSeamlessUriScheme(uri: Uri): String?
```

Полученное значение необходимо передать в следующий метод билдера **SberIDLoginBuilder** при построении диплинка авторизации.

```java
/**
* Кастомная схема диплинка авторизации по Сбер ID в формате app://host,
* используется к примеру при бесшовной авторизации по Сбер ID
* Для стандартной авторизации по кнопке не требуется
*
* @param uriScheme схема диплинка авторизации по Сбер ID в формате app://host
*/
fun uriScheme(uriScheme: String?): SberIDBuilder
```

Все дальнейшие действия по подготовке диплинка и старте авторизации аналогичны описанным в разделе Запуск авторизации по Сбер ID.

### Запуск авторизации Сбер ID в CustomTabs

Начиная с версии SDK 1.4.0, была добавлена поддержка запуска авторизации по Сбер ID через веб-страницу oidc, которая открывается в CustomTabs. Теперь, если приложение СберБанк Онлайн не установлено на устройстве, веб-страница авторизации откроется в CustomTabs, а не во внешнем браузере, как это было раньше. Это позволяет реализовать еще более бесшовный сценарий для клиента без редиректов в отдельное приложение.

Для этого в вашем проекте должна быть добавлена зависимость на библиотеку браузера AndroidX. 

Для этого в вашем build.gradle пропишите ее в разделе зависимости, если ее еще нет (версия указана на момент публикации, можно использовать более актуальные):

```java
dependencies {
    implementation "androidx.browser:browser:1.3.0"
}
```

### Сбер ID через единое веб окно авторизации

В версии SDK 1.4.0 была добавлен новый метод для авторизации пользователя по Сбер ID, используя единое веб окно авторизации. Данный метод предназначен для партнеров, которым необходимо по той или иной причине производить авторизацию только через веб.

?> Для этого нужно чтобы была подключена зависимость на библиотеку androidx.browser и версия sdk от 1.4.0. **Не используйте** этот способ, если вам нужен обычный вход через Сбер ID.

**Как это работает:**

Создайте Uri, как при обычном входе с дополнительным параметром **customTabRedirectUri**. Для этого в **SberIDLoginManager.sberIDBuilder()** появился новый метод **customTabRedirectUri(customTabRedirectUri: String, context: Context): SberIDBuilder**:

```java
val uri = SberIDLoginManager
	.sberIDBuilder()
    .clientID("512e1152-e14c-5223-a125-d519eb68bb86")
    .scope("openid name email mobile birthdate gender")
    .state("ffad1d59c1e34844a1415226103d44f3")
    .nonce("b1947d4f10a24eb0a21155239be9b066")
    .redirectUri("app://merchant_app/")
	.customTabRedirectUri("app://redirect_app/", this)
    .codeChallenge(codeChallenge) //Необязательный параметр
    .codeChallengeMethod(PkceUtils.getCodeChallengeMethod()) //Необязательный параметр
    .build()
```

Запустите авторизацию, используя функцию **loginWithSberIDToCustomTabs(context: Context, uri: Uri): Boolean**, класса **SberIDLoginManager**. Если запуск сценария невозможен, вернется false, данное поведение возможно, если не были подключены все необходимые зависимости, нет подходящих браузеров на устройстве или ссылка была собрана некорректно.

Если все сделано верно, вы увидите окно с различными способами идентификации входа по Сбер ID: 

![Скриншот входа](images/screen_auth_by_sbol.png ':size=600')

Новый параметр **СustomTabRedirectUri** используется для возврата в ваше приложение в сценариях: **"Вход по номеру телефона"**, **"Войти через Сбербанк Онлайн"** и других. Она запускает заглушечную activity, которая открывается поверх окна с CustomTabs и сразу закрывается. Тем самым происходит возврат на то самое веб окно с CustomTabs, с которого начиналась авторизация в вебе. В SDK идет готовая заглушечная activity **ReturnToCustomTabsSberIDActivity**, чтобы использовать ее **добавьте activity в вашем AndroidManifest.xml**:

```java
 <activity
            android:name="sberid.sdk.auth.view.activity.ReturnToCustomTabsSberIDActivity"
            android:screenOrientation="portrait"
            android:launchMode="singleTask">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />

                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />

                <data
                    android:host="redirect_app"
                    android:scheme="app" />
            </intent-filter>
        </activity>
```

?> **android:host** и **android:scheme** соответсвуют **customTabRedirectUri** из предыдущих шагов. Регистрация на стороне банка **customTabRedirectUri** аналогичная той, что используется для redirectUri. **android:launchMode="singleTask"** позволяет вернуться в ваше приложение вне задачи мобильного приложения СберБанк Онлайн.

Окно с CustomTabs после восстановления продолжит свою работу и в результате, как и раньше, после авторизации пользователя, по диплинку из переданного изначально redirectUri, произойдет редирект на нужный экран вашего приложения. 

Смотрите пункт **Запуск авторизации по Сбер ID**.

### Описание ошибок

!> Если state сгенерированный вами и state возвращенный при авторизации по Сбер ID не совпадет, результат будет ошибочным, в SberIDResultModel.errorDescription вернется фраза "invalid_state". При других ошибках, так как падение приложения Сбербанк Онлайн, прерывание сценария и др. будет ошибка "internal_error"

**Пример ответа с ошибкой**

```java
appScheme://appName/appPath?result=FAILURE&error_code=5
```

|типы возвращаемых ошибок|описание ошибок|
|:---------:|:---------:|
|invalid_request|В запросе отсутствуют обязательные атрибуты.|
|unauthorized_client|АС-источник запроса не зарегистрирована в банке.|
|unauthorized_client|АС-источник запроса заблокирована в банке.|
|unauthorized_client|Значение атрибута client_id не соответствует формату.|
|unsupported_response_type|Значение атрибута response_type не равно «code».|
|invalid_scope|Запрошенный scope содержит значения, недоступные для АС-источника запроса.|
|invalid_request|Значение code_challenge_method не соответствуют допустимым значениям.|

!> 
- Для любого из перечисленных типов ошибок МП СБОЛ на платформе Android возвращает в приложение партнера код 5 в параметре error_code без указания типа ошибки.
- В случае отсутствия в запросе атрибута redirect_uri или в случае если значение redirect_uri не зарегистрировано для данного партнера, банк перенаправляет клиента на экран, информирующий клиента о недоступности сервиса.


