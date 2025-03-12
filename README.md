sdk-aos
=======

LiqPay Android SDK

Documentation https://www.liqpay.ua/documentation/en

Please look at our Liqpay documentation - you can find there how to use Gpay and Privatpay method

https://www.liqpay.ua/documentation

Please, read https://www.liqpay.ua/en/faq before you start use Liqpay as acquire

Also if you need card method or other - you can use web-view checkout page in your App

Also we have some SDK for Android working with Masterpass, if you're interested - please send email to technical.support@liqpay.ua

Adding dependency
=======
1. Download project as a zip file, it contains `repo` folder with library and its dependencies
2. Add `repo` folder to your root project directory
3. Inside your project at `settings.gradle` under `dependencyResolutionManagement` add `repo` folder as a maven repository
```
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        //...
        maven { url 'repo' }
    }
}
```
4. At module-level `build.gradle` add `implementation("ua.privatbank:liqpay_x:1.0")`
