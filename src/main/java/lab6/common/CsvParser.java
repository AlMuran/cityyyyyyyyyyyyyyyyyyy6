package lab6.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Утилитарный класс для парсинга CSV-строк с поддержкой экранирования.
 *
 * <p>Позволяет разбирать строки в формате CSV, где значения могут быть
 * заключены в кавычки, а кавычки внутри значений экранируются двойными кавычками.</p>
 *
 * <p>Пример использования:
 * <pre>
 * String line = "add,\"Москва, столица\",10,55.75";
 * String[] fields = CsvParser.parseCsvLine(line);
 * // fields = ["add", "Москва, столица", "10", "55.75"]
 * </pre>
 * </p>
 *
 * <p>Также предоставляет метод {@link #escapeCsv(String)} для экранирования строк
 * при сохранении в CSV-формат.</p>
 *
 * @author AlMuran
 * @version 1.0
 * @since 1.0
 * @see lab6.server.managers.FileManager
 * @see lab6.client.ConsoleManager
 */
public final class CsvParser {

    /**
     * Приватный конструктор для утилитарного класса.
     * <p>Запрещает создание экземпляров класса.</p>
     */
    private CsvParser() {}

    /**
     * Разбирает CSV-строку на массив полей с учётом экранирования.
     *
     * <p>Правила парсинга:
     * <ul>
     *   <li>Запятая разделяет поля</li>
     *   <li>Двойные кавычки используются для экранирования</li>
     *   <li>Внутри кавычек запятые не разделяют поля</li>
     *   <li>Две двойные кавычки подряд ("") превращаются в одну двойную кавычку внутри значения</li>
     * </ul>
     * </p>
     *
     * <p>Примеры:
     * <pre>
     * parseCsvLine("hello,world")           → ["hello", "world"]
     * parseCsvLine("hello,\"world, big\"")  → ["hello", "world, big"]
     * parseCsvLine("\"hello\"\"world\"")    → ["hello\"world"]
     * </pre>
     * </p>
     *
     * @param line CSV-строка для парсинга (не может быть null)
     * @return массив строк с полями (может быть пустым)
     */
    public static String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {

                    current.append('"');
                    i++;
                } else {

                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {

                result.add(current.toString());
                current.setLength(0);
            } else {

                current.append(c);
            }
        }


        result.add(current.toString());
        return result.toArray(new String[0]);
    }

    /**
     * Экранирует строку для безопасного сохранения в CSV-формат.
     *
     * <p>Строка заключается в двойные кавычки, если она содержит:
     * <ul>
     *   <li>запятые (,) — чтобы не сломать разделение полей</li>
     *   <li>двойные кавычки (") — экранируются как две кавычки</li>
     *   <li>переводы строк (\n) — чтобы сохранить структуру</li>
     * </ul>
     * </p>
     *
     * <p>Примеры:
     * <pre>
     * escapeCsv("Москва")           → "Москва" (без кавычек, т.к. нет спецсимволов)
     * escapeCsv("Москва, Россия")   → "\"Москва, Россия\"" (в кавычках)
     * escapeCsv("Он сказал \"Привет\"") → "\"Он сказал \"\"Привет\"\"\"" (кавычки экранированы)
     * </pre>
     * </p>
     *
     * @param field строка для экранирования (может быть null)
     * @return экранированная строка, готовая для записи в CSV
     */
    public static String escapeCsv(String field) {
        if (field == null) return "";


        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {

            return "\"" + field.replace("\"", "\"\"") + "\"";
        }

        return field;
    }
}