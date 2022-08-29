package ru.netology.delivery.test;

import com.codeborne.selenide.Configuration;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;


import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$;

public class CardDeliveryChangeDateTest {
    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Nested
    @DisplayName("Replan Delivery and Checkbox")
    class HappyPathAndCheckbox {

        @BeforeEach
        void setUpAll() {
            var validUser = DataGenerator.Registration.generateUser("ru");
            var daysToAddForFirstMeeting = 4;
            var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
            var daysToAddForSecondMeeting = 7;
            var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);
            Configuration.holdBrowserOpen = true;
            open("http://localhost:9999");
            $("[data-test-id='date'] input").doubleClick().sendKeys(Keys.DELETE);
            $x("//input[@placeholder='Город']").setValue(validUser.getCity());
            $("[data-test-id='date'] input").setValue(firstMeetingDate);
            $("[data-test-id='name'] input").setValue(validUser.getName());
            $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        }

        @Test
        void shouldReplanByHappyPath() {
            var daysToAddForFirstMeeting = 4;
            var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
            var daysToAddForSecondMeeting = 7;
            var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);
            $(withText("Я соглашаюсь с условиями обработки и")).click();
            $x("//*[text()=\"Запланировать\"]").click();
            $("[data-test-id='success-notification']")
                    .shouldBe(text("Успешно! Встреча успешно запланирована на " + firstMeetingDate), Duration.ofSeconds(15));
            $("[data-test-id='date'] input").doubleClick().sendKeys(Keys.DELETE);
            $("[data-test-id='date'] input").setValue(secondMeetingDate);
            $x("//*[text()=\"Запланировать\"]").click();
            $("[data-test-id='replan-notification']")
                    .shouldBe(text("У вас уже запланирована встреча на другую дату. Перепланировать? "), Duration.ofSeconds(15));
            $x("//*[text()=\"Перепланировать\"]").click();
            $("[data-test-id='success-notification']")
                    .shouldBe(text("Встреча успешно запланирована на  " + secondMeetingDate), Duration.ofSeconds(15));
        }

        @Test
        void shouldNoCheckbox() {
            $x("//*[text()=\"Запланировать\"]").click();
            $("[data-test-id='agreement'].input_invalid .checkbox__text")
                    .shouldBe(text("Я соглашаюсь с условиями обработки и использования моих персональных данных"));
        }
    }

    @Nested
    @DisplayName("City validation")
    class CityValidation {
        @BeforeEach
        void setUpAll() {
            Configuration.holdBrowserOpen = true;
            open("http://localhost:9999");
            $("[data-test-id='date'] input").doubleClick().sendKeys(Keys.DELETE);
            $(withText("Я соглашаюсь с условиями обработки и")).click();
            var validUser = DataGenerator.Registration.generateUser("ru");
            var daysToAddForFirstMeeting = 4;
            var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
            $("[data-test-id='date'] input").setValue(firstMeetingDate);
            $("[data-test-id='name'] input").setValue(validUser.getName());
            $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        }

        @Test
        void shouldChooseCityWithNoDelivery() {
            var validUser = DataGenerator.Registration.generateUser("ru");
            var invalidUser = DataGenerator.Registration.generateInvalidUser("ru");
            $x("//input[@placeholder='Город']").setValue(invalidUser.getCity());
            $x("//*[text()=\"Запланировать\"]").click();
            $("[data-test-id='city'].input_invalid .input__sub")
                    .shouldBe(text("Доставка в выбранный город недоступна "));
        }

        @Test
        void shouldSetCityInLatin() {
            var validUser = DataGenerator.Registration.generateUser("ru");
            var invalidUser = DataGenerator.Registration.generateUserByFaker("en");
            $x("//input[@placeholder='Город']").setValue(invalidUser.getCity());
            $x("//*[text()=\"Запланировать\"]").click();
            $("[data-test-id='city'].input_invalid .input__sub")
                    .shouldBe(text("Доставка в выбранный город недоступна "));
        }

        @Test
        void shouldSetNoCity() {
            $x("//*[text()=\"Запланировать\"]").click();
            $("[data-test-id='city'].input_invalid .input__sub")
                    .shouldBe(text("Поле обязательно для заполнения "));
        }

        @Test
        void shouldSetCityByWhitespaces() {
            $x("//input[@placeholder='Город']").setValue("                ");
            $x("//*[text()=\"Запланировать\"]").click();
            $("[data-test-id='city'].input_invalid .input__sub")
                    .shouldBe(text("Поле обязательно для заполнения "));
        }

        @Test
        void shouldSetNumbersInCityField() {
            var validUser = DataGenerator.Registration.generateUser("ru");
            $x("//input[@placeholder='Город']").setValue(validUser.getPhone());
            $x("//noscript").shouldHave(exactText(""));
        }
    }

    @Nested
    @DisplayName("Date validation")
    class DateValidation {
        @BeforeEach
        void setUpAll() {
            Configuration.holdBrowserOpen = true;
            open("http://localhost:9999");
            $("[data-test-id='date'] input").doubleClick().sendKeys(Keys.DELETE);
            $(withText("Я соглашаюсь с условиями обработки и")).click();
            var validUser = DataGenerator.Registration.generateUser("ru");
            $x("//input[@placeholder='Город']").setValue(validUser.getCity());
            $("[data-test-id='name'] input").setValue(validUser.getName());
            $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        }

        @Test
        void shouldChooseDateInPast() {
            var validUser = DataGenerator.Registration.generateUser("ru");
            var daysToAddForFirstMeeting = -4;
            var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
            $("[data-test-id='date'] input").setValue(firstMeetingDate);
            $x("//*[text()=\"Запланировать\"]").click();
            $("[data-test-id='date'] .input_invalid .input__sub")
                    .shouldBe(text("Заказ на выбранную дату невозможен"));
        }

        @Test
        void shouldChooseDateLessThanThreeDays() {
            var validUser = DataGenerator.Registration.generateUser("ru");
            var daysToAddForFirstMeeting = 2;
            var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
            $("[data-test-id='date'] input").setValue(firstMeetingDate);
            $x("//*[text()=\"Запланировать\"]").click();
            $("[data-test-id='date'] .input_invalid .input__sub")
                    .shouldBe(text("Заказ на выбранную дату невозможен"));
        }

        @Test
        void shouldChooseDateInFarFuture() {
            var validUser = DataGenerator.Registration.generateUser("ru");
            var daysToAddForFirstMeeting = 200000;
            var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
            $("[data-test-id='date'] input").setValue(firstMeetingDate);
            $x("//*[text()=\"Запланировать\"]").click();
            $("[data-test-id='success-notification']")
                    .shouldBe(text("Успешно! Встреча успешно запланирована на " + firstMeetingDate), Duration.ofSeconds(15));
        }

        @Test
        void shouldChooseNoDate() {
            var validUser = DataGenerator.Registration.generateUser("ru");
            $x("//*[text()=\"Запланировать\"]").click();
            $("[data-test-id='date'] .input_invalid .input__sub")
                    .shouldBe(text("Неверно введена дата"));
        }
    }

    @Nested
    @DisplayName("Name validation")
    class NameValidation {
        @BeforeEach
        void setUpAll() {
            Configuration.holdBrowserOpen = true;
            open("http://localhost:9999");
            $("[data-test-id='date'] input").doubleClick().sendKeys(Keys.DELETE);
            $(withText("Я соглашаюсь с условиями обработки и")).click();
            var validUser = DataGenerator.Registration.generateUser("ru");
            var daysToAddForFirstMeeting = 4;
            var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
            $x("//input[@placeholder='Город']").setValue(validUser.getCity());
            $("[data-test-id='date'] input").setValue(firstMeetingDate);
            $("[data-test-id='phone'] input").setValue(validUser.getPhone());
        }

        @Test
        void shouldSetLatinName() {
            var invalidUser = DataGenerator.Registration.generateUserByFaker("en");
            $("[data-test-id='name'] input").setValue(invalidUser.getName());
            $x("//*[text()=\"Запланировать\"]").click();
            $("[data-test-id='name'].input_invalid .input__sub")
                    .shouldBe(text("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
        }

        @Test
        void shouldSetNumbersInName() {
            var validUser = DataGenerator.Registration.generateUser("ru");
            $("[data-test-id='name'] input").setValue(validUser.getPhone());
            $x("//*[text()=\"Запланировать\"]").click();
            $("[data-test-id='name'].input_invalid .input__sub")
                    .shouldBe(text("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
        }

        @Test
        void shouldSetOneLetterInName() {
            var daysToAddForFirstMeeting = 4;
            var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
            $("[data-test-id='name'] input").setValue("Ы");
            $x("//*[text()=\"Запланировать\"]").click();
            $("[data-test-id='name'].input_invalid .input__sub")
                    .shouldBe(text("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы. В данном поле допустимы не менее 2 символов"));
        }

        @Test
        void shouldSetNameWithDash() {
            var daysToAddForFirstMeeting = 4;
            var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
            $("[data-test-id='name'] input").setValue("Мария Склодовская-Кюри");
            $x("//*[text()=\"Запланировать\"]").click();
            $("[data-test-id='success-notification']")
                    .shouldBe(text("Успешно! Встреча успешно запланирована на " + firstMeetingDate), Duration.ofSeconds(15));
        }

        @Test
        void shouldSetNameWithYoLetter() {
            var daysToAddForFirstMeeting = 4;
            var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
            $("[data-test-id='name'] input").setValue("Артём Семёнов");
            $x("//*[text()=\"Запланировать\"]").click();
            $("[data-test-id='success-notification']")
                    .shouldBe(text("Успешно! Встреча успешно запланирована на " + firstMeetingDate), Duration.ofSeconds(15));
        }

        @Test
        void shouldSetNoName() {
            $x("//*[text()=\"Запланировать\"]").click();
            $("[data-test-id='name'].input_invalid .input__sub")
                    .shouldBe(text("Поле обязательно для заполнения"));
        }
    }

    @Nested
    @DisplayName("Phone Validation")
    class PhoneValidation {
        @BeforeEach
        void setUpAll() {
            Configuration.holdBrowserOpen = true;
            open("http://localhost:9999");
            $("[data-test-id='date'] input").doubleClick().sendKeys(Keys.DELETE);
            $(withText("Я соглашаюсь с условиями обработки и")).click();
            var validUser = DataGenerator.Registration.generateUser("ru");
            var daysToAddForFirstMeeting = 4;
            var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
            $x("//input[@placeholder='Город']").setValue(validUser.getCity());
            $("[data-test-id='date'] input").setValue(firstMeetingDate);
            $("[data-test-id='name'] input").setValue(validUser.getName());
        }

        @Test
        void shouldSetPhoneWithWhitespaces() {
            var daysToAddForFirstMeeting = 4;
            var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
            $("[data-test-id='phone'] input").setValue("+7 951 682 71 44");
            $x("//*[text()=\"Запланировать\"]").click();
            $("[data-test-id='success-notification']")
                    .shouldBe(text("Успешно! Встреча успешно запланирована на " + firstMeetingDate), Duration.ofSeconds(15));
        }

        @Test
        void shouldSetShortPhoneNumber() {
            var daysToAddForFirstMeeting = 4;
            var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
            $("[data-test-id='phone'] input").setValue("7");
            $x("//*[text()=\"Запланировать\"]").click();
            $("[data-test-id='phone'].input_invalid .input__sub")
                    .shouldBe(text("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
        }

        @Test
        void shouldSetLongPhoneNumber() {
            var daysToAddForFirstMeeting = 4;
            var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
            $("[data-test-id='phone'] input").setValue("7 951 682 71 44 682 71 44 682 71 44");
            $x("//*[text()=\"Запланировать\"]").click();
            $("[data-test-id='success-notification']")
                    .shouldBe(text("Успешно! Встреча успешно запланирована на " + firstMeetingDate), Duration.ofSeconds(15));
        }

        @Test
        void shouldSetNoPhone() {
            $x("//*[text()=\"Запланировать\"]").click();
            $("[data-test-id='phone'].input_invalid .input__sub")
                    .shouldBe(text("Поле обязательно для заполнения"));
        }
    }
}


