# sdk-aos

LiqPay Android SDK

Please refer to our [Liqpay documentation](https://www.liqpay.ua/doc) — there you will find information on how to use Gpay and Privatpay methods.

Please read [https://www.liqpay.ua/en/information/handbook](https://www.liqpay.ua/en/information/handbook) before starting to use Liqpay as an acquirer.

Also, if you need a card payment method or another option — you can use the web checkout page in your application.

We also provide an Android SDK with Masterpass support. If you are interested — please contact us at liqpay.support@privatbank.ua.

---

# Adding Dependency to the Project

1. Download the project as a `.zip` file, which contains a `repo` folder with the library and its transitive dependencies.
2. Add the `repo` folder to the root directory of your project.
3. In your `settings.gradle` file, under the `dependencyResolutionManagement` block, add the `repo` folder as a maven repository:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        // Add the `repo` folder as a repository
        maven { url = uri("repo") }
    }
}
```

4. In the module-level `build.gradle` (e.g., `:app`), add:
```kotlin
implementation("ua.privatbank:liqpay_x:1.0")
```

---

# Initialization

Before calling payment methods, you must initialize the SDK in your `Application` class with `LiqPay.init(this)`:

```kotlin
class SampleApp : Application() {
    override fun onCreate() {
        super.onCreate()
        LiqPay.init(this)
    }
}
```

---

# Creating Payment Data

To perform a payment operation, you need to generate `data` and `signature`.  
You can do this at: [https://www.liqpay.ua/doc/forming_test_data](https://www.liqpay.ua/doc/forming_test_data)

---

# Available Payment Methods

## 1. P24Pay

Payment is processed using the Privat24 app (`ua.privatbank.ap24`).  
Initialize the object `LiqPay.p24Pay(this)` (accepts `Activity` or `Fragment`) and call:

```kotlin
confirmPay(data, signature)
```

**Callbacks:**
- `onSuccess()` – triggered on successful payment
- `onError(LiqpayException)` – triggered on error. Possible exceptions:
    - `AppNotExistException` – Privat24 app not installed
    - `NoInternetConnectionException` – No Internet connection
    - `IncorrectDataException` – Invalid input data

**Usage Example:**
```kotlin
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

## Other Payment Methods

Are built on `androidx.activity.result.contract.ActivityResultContract`.

Call either `LiqPay.cardPay().createActivityResultContract()` or `LiqPay.tokenPay().createActivityResultContract()` and register using `registerForActivityResult()`:

```kotlin
private val cardPayActivityResultLauncher =
        registerForActivityResult(LiqPay.cardPay().createActivityResultContract()) {
            handleActivityResult(it)
        }
```

The obtained `ActivityResultLauncher` is ready to use — it accepts an `InputData(data, signature)` object.

It returns an `ActivityResult`. If the result code is `RESULT_OK`, you can extract the `LiqpayActivityResult` from the intent using `EXTRA_LIQPAY_RESULT`.

```kotlin
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

### LiqpayActivityResult

`LiqpayActivityResult` contains the following fields:
- `val payment: PaymentResponse.Payment? = null` – present if successful
- `val exception: java.lang.Exception? = null` – present if there was an error

The `PaymentResponse.Payment` object contains:
- confirmToken
- paymentId
- status
- errCode
- errDescription

## 2. Card Payment Example

```kotlin
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
}
```

## 3. Token Payment Example

```kotlin
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
}
```

## 4. Google Pay Payment

1. Initialize the object `googlePayProvider: GooglePayProvider`.  
   Call `googlePayProvider.init()` with appropriate `data` and `signature`.

**Callbacks:**
- `onSuccess(GooglePayInitResponse)` – triggered on successful init, needed for further processing
- `onError(LiqpayException)` – in case of error
- `onTerminate()` – for convenience, triggered after either success or error

2. Obtain the `payment token` using the Google Pay API:  
   [https://developers.google.com/pay/api/android/overview](https://developers.google.com/pay/api/android/overview)

3. Get the corresponding `ActivityResultLauncher` via `googlePayProvider.createActivityResultContract()` and handle the result. See [Other Payment Methods](#other-payment-methods)

```kotlin
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
