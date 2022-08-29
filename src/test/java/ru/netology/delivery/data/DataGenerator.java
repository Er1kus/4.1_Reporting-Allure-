package ru.netology.delivery.data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;

public class DataGenerator {
    private DataGenerator() {
    }

    public static String generateDate(int shift) {
        return LocalDate.now().plusDays(shift).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public static String generateCity(String locale) {
        var random = new SecureRandom();
        var list = Arrays.asList("Майкоп", "Горно-Алтайск", "Уфа", "Улан-Удэ",
                "Махачкала", "Магас", "Нальчик", "Элиста", "Черкесск", "Петрозаводск", "Сыктывкар", "Симферополь",
                "Йошкар-Ола", "Саранск", "Якутск", "Владикавказ", "Казань", "Кызыл", "Ижевск", "Абакан", "Грозный",
                "Чебоксары", "Барнаул", "Чита", "Петропавловск-Камчатский", "Краснодар", "Красноярск", "Пермь",
                "Владивосток", "Ставрополь", "Хабаровск", "Благовещенск", "Архангельск", "Астрахань", "Белгород",
                "Брянск", "Владимир", "Волгоград", "Вологда", "Воронеж", "Иваново", "Иркутск", "Калининград", "Калуга",
                "Кемерово", "Киров", "Кострома", "Курган", "Курск", "Липецк", "Магадан",
                "Мурманск", "Нижний Новгород", "Великий Новгород", "Новосибирск", "Омск", "Оренбург", "Орёл", "Пенза",
                "Псков", "Ростов-на-Дону", "Рязань", "Самара", "Саратов", "Южно-Сахалинск", "Екатеринбург", "Смоленск",
                "Тамбов", "Тверь", "Томск", "Тула", "Тюмень", "Ульяновск", "Челябинск", "Ярославль", "Москва",
                "Санкт-Петербург", "Севастополь", "Биробиджан", "Нарьян-Мар", "Ханты-Мансийск", "Анадырь", "Салехард");
        var randomElement = list.get(random.nextInt(list.size()));
        return (randomElement);
    }

    public static String generateInvalidCity(String locale) {
        var random = new SecureRandom();
        var list = Arrays.asList("Гатчина", "Пушкин", "Сестрорецк", "Дели", "Брисбен", "Белу-Оризонти", "Гвадалахара", "Лиссабон", "Хараре", "Диксон");
        var randomElement = list.get(random.nextInt(list.size()));
        return (randomElement);
    }

    public static String generateInvalidEngCity(String locale) {
        Faker faker = new Faker(new Locale(locale));
        return faker.address().cityName();
    }

    public static String generateName(String locale) {
        Faker faker = new Faker(new Locale(locale));
        return faker.name().fullName();
    }

    public static String generatePhone(String locale) {
        Faker faker = new Faker(new Locale(locale));
        return faker.phoneNumber().phoneNumber();
    }

    public static class Registration {
        private Registration() {
        }

        public static UserInfo generateUser(String locale) {
            return new UserInfo(generateCity(locale), generateName(locale), generatePhone(locale));
        }

        public static UserInfo generateInvalidUser(String locale) {
            return new UserInfo(generateInvalidCity(locale), generateName(locale), generatePhone(locale));
        }

        public static UserInfo generateUserByFaker(String locale) {
            return new UserInfo(generateInvalidEngCity(locale), generateName(locale), generatePhone(locale));
        }

        @Value
        public static class UserInfo {
            String city;
            String name;
            String phone;
        }
    }
}




