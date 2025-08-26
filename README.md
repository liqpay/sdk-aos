sdk-aos
=======

LiqPay Android SDK

Будь ласка, ознайомтесь з нашою документацією Liqpay — там ви знайдете інформацію про те, як використовувати методи Gpay та Privatpay.

https://www.liqpay.ua/doc

Будь ласка, прочитайте https://www.liqpay.ua/en/information/handbook перед тим, як почати використовувати Liqpay як еквайєра.

Також, якщо вам потрібен метод оплати карткою або інший — ви можете використовувати веб-сторінку оформлення платежу (checkout) у вашому застосунку.

Також ми маємо SDK для Android із підтримкою Masterpass. Якщо ви зацікавлені — зверніться, будь ласка, за адресою liqpay.support@privatbank.ua.

Додання залежності у проект
=======

1. Викачати проект як .zip файл, він містить папку `repo` з бібліотекою та її транзитивними залежностями.
2. Додати папку `repo` у кореневу директорію проекту.
3. У файлі проекту `settings.gradle` у блоці `dependencyResolutionManagement` додати папку `repo` як maven репозиторій
```
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        //Додання папки `repo` у якості репозиторія
        maven { url = uri("repo") }
    }
}
```
4. У `build.gradle` на рівні модуля (:app) додати `implementation("ua.privatbank:liqpay_x:1.0")`

Ініціалізація
=======

Перед викликом методів оплати потрібно викликати ініціалізацію у `Application` класі застосунку - `LiqPay.init(this)`

```
class SampleApp : Application() {
    override fun onCreate() {
        super.onCreate()
        LiqPay.init(this)
    }
}
```

Формування даних для оплати
=======
Для виконання операції оплати потрібно сформувати `data` та `signature`, можна зробити за посиланням https://www.liqpay.ua/doc/forming_test_data

Доступні методи оплати
=======

**1. P24Pay**

Для оплати використовується застосунок Privat24 (ua.privatbank.ap24).  
Ініціалізувати обʼєкт `LiqPay.p24Pay(this)` (приймає `Activity` або `Fragment`) та викликати метод
`confirmPay(data, signature)`
Колбеки:
- `onSuccess()` - викликається при успішній оплаті
- `onError(LiqpayException)` - у разі помилок. Можливі помилки:
    - `AppNotExistException` – застосунок Privat24 не встановлений
    - `NoInternetConnectionException` - відсутнє підключення до Інтернету
    - `IncorrectDataException` - проблема із вхідними даними


Приклад використання:
```
import ua.privatbank.liqpay.LiqPay

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        button.setOnClickListener {
            LiqPay.p24Pay(this)
                .confirmPay(
                    PaymentData.p24PayDataAndSignature.first,
                    PaymentData.p24PayDataAndSignature.second
                )
                .onSuccess {
                    Handler(Looper.getMainLooper()).post {
                        tvResult.text = "onSuccess p24 pay"
                    }
                }
                .onError {
                    Handler(Looper.getMainLooper()).post {
                        tvResult.text = "onError p24 pay; {$it}";
                    }
                }
        }
    }
}
```


## Інші методи оплати


Використовують механізм `androidx.activity.result.contract.ActivityResultContract`  
Викликати `LiqPay.cardPay().createActivityResultContract()` або `LiqPay.tokenPay().createActivityResultContract()`  та зарєєструвати за допомогою `registerForActivityResult()`

```
private val cardPayActivityResultLauncher =
        registerForActivityResult(LiqPay.cardPay().createActivityResultContract()) {
            handleActivityResult(it)
        }
```

Отриманний `ActivityResultLauncher` готовий для використання, на вхід приймає обʼєкт `InputData(data, signature)`.  
Повертає `ActivityResult`. Якщо операція пройшла успішно та отримано `resultCode: RESULT_OK`, то по константі `EXTRA_LIQPAY_RESULT` можно отримати `LiqpayActivityResult`

```
private fun handleActivityResult(it: ActivityResult) {
        if (it.resultCode == RESULT_OK) {
            val intent = it.data ?: return
            val result =
                intent.getSerializableExtra(EXTRA_LIQPAY_RESULT) as LiqpayActivityResult
            val text = "Got result OK with data: $result"
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
            tvResult.text = text
        } else if (it.resultCode == RESULT_CANCELED) {
            val text = "Got RESULT_CANCELED"
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
            tvResult.text = text
        }
    }
```

**LiqpayActivityResult**

`LiqpayActivityResult` містить наступні поля
- `val payment: PaymentResponse.Payment? = null` - наявне в результаті успіху
- `val exception: java.lang.Exception? = null` - навяне в резульаті помилки

В свою чергу `PaymentResponse.Payment` має:
- confirmToken
- paymentId
- status
- errCode
- errDescription


**2. Приклад оплати картою**

```
class MainActivity : AppCompatActivity() {

    private val cardPayActivityResultLauncher =
        registerForActivityResult(LiqPay.cardPay().createActivityResultContract()) {
            handleActivityResult(it)
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            btnLiqpayCard.setOnClickListener {
                cardPayActivityResultLauncher.launch(
                    InputData(
                        PaymentData.dataCardPay.first,
                        PaymentData.dataCardPay.second
                    )
                )
            }
        }

    private fun handleActivityResult(it: ActivityResult) {
        if (it.resultCode == RESULT_OK) {
            val intent = it.data ?: return
            val payment =
                intent.getSerializableExtra(EXTRA_LIQPAY_RESULT) as LiqpayActivityResult
            val text = "Got result OK with data: $payment"
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
            tvResult.text = text
        } else if (it.resultCode == RESULT_CANCELED) {
            val text = "Got RESULT_CANCELED"
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
            tvResult.text = text
        }
    }

```

**3. Приклад оплати токеном**

```
class MainActivity : AppCompatActivity() {

    private val tokenPayActivityResultLauncher =
        registerForActivityResult(LiqPay.tokenPay().createActivityResultContract()) {
            handleActivityResult(it)
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            btnLiqpayToken.setOnClickListener {
                tokenPayActivityResultLauncher.launch(
                    InputData(
                        PaymentData.dataLiqpayToken.first,
                        PaymentData.dataLiqpayToken.second
                    )
                )
            }
        }

    private fun handleActivityResult(it: ActivityResult) {
        if (it.resultCode == RESULT_OK) {
            val intent = it.data ?: return
            val payment =
                intent.getSerializableExtra(EXTRA_LIQPAY_RESULT) as LiqpayActivityResult
            val text = "Got result OK with data: $payment"
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
            tvResult.text = text
        } else if (it.resultCode == RESULT_CANCELED) {
            val text = "Got RESULT_CANCELED"
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
            tvResult.text = text
        }
    }
```

**4.  Оплата за допомогою GooglePay**

1. Ініціалізувати обʼєкт `googlePayProvider: GooglePayProvider`.  
   Викликати `googlePayProvider.init()` із відповідними `data` та `signature`
   Колбеки:
- `onSuccess(GooglePayInitResponse)` - викликається при успішній ініціалізації, потрібен для подальшого запуску механізму оплати
- `onError(LiqpayException)` - у разі помилок.
- `onTerminate()` - для зручності, викликається післа `onSuccess()` та `onError()`

2. Далі необхідно самостійно отримати `payment token` за допомогою Google Pay API - [https://developers.google.com/pay/api/android/overview](https://developers.google.com/pay/api/android/overview)

3. Отримати відповідний `ActivityResultLauncher` за допомогою `googlePayProvider.createActivityResultContract()` та обробити результат. Див розділ [Інші методи оплати](#інші-методи-оплати)

```
   private val googleTokenPayActivityResultLauncher =
        registerForActivityResult(googlePayProvider.createActivityResultContract()) {
            handleActivityResult(it)
        }

    private fun handleActivityResult(it: ActivityResult) {
        if (it.resultCode == RESULT_OK) {
            val intent = it.data ?: return
            val result =
                intent.getSerializableExtra(EXTRA_LIQPAY_RESULT) as LiqpayActivityResult
            val text = "Got result OK with data: $result"
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        } else if (it.resultCode == RESULT_CANCELED) {
            val text = "Got RESULT_CANCELED"
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        }
    }
```