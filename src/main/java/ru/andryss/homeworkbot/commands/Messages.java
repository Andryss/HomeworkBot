package ru.andryss.homeworkbot.commands;

/**
 * Class containing all messages the bot can send
 */
public class Messages {

    private Messages() {
        throw new UnsupportedOperationException("util class");
    }

    // Dispatcher blok
    public static final String DISPATCHER_NO_COMMAND = "К сожалению, я умею отвечать только на команды\n/help";
    public static final String DISPATCHER_UNKNOWN_COMMAND = "Такой команды я не знаю\n/help";
    public static final String DISPATCHER_ERROR = "При обработке команды произошла неизвестная ошибка, попробуйте еще раз позже";
    public static final String DISPATCHER_HANDLER_ERROR = "Во время работы команды произошла неизвестная ошибка, попробуйте еще раз позже";

    // Common messages block
    public static final String REGISTER_FIRST = "Для начала зарегистрируйтесь\n/start";
    public static final String NOT_LEADER = "Вы не являетесь старостой";
    public static final String NO_TOPICS = "Нет ни одного домашнего задания";
    public static final String TOPICS_LIST = "Список домашних заданий:\n%s";
    public static final String TOPIC_NOT_FOUND = "Пожалуйста, введите название существующего домашнего задания:";
    public static final String ASK_FOR_RESENDING_TOPIC = "Пожалуйста, введите название домашнего задания текстом:";
    public static final String YES_ANSWER = "да";
    public static final String NO_ANSWER = "нет";
    public static final String ASK_FOR_RESENDING_CONFIRMATION = "Пожалуйста, выберите \"да\" или \"нет\":";
    public static final String STOP_WORD = "стоп-слово";


    // Specific messages block (starts with "<COMMAND_NAME>_")
    public static final String START_ASK_FOR_FIRSTNAME_LASTNAME = "Пожалуйста, введите ФИО, так будут подписаны ваши работы\n(например, \"Иванов Иван Иванович\"):";
    public static final String START_ILLEGAL_CHARACTERS = "ФИО содержит недопустимые символы\n(допустимые символы: буквы, пробел, \"-\")";
    public static final String START_TOO_MANY_CHARACTERS = "ФИО должно состоять из максимум 70 символов!";
    public static final String START_ALREADY_REGISTERED = "Кто-то уже зарегистрировался под таким ФИО";
    public static final String START_ANSWER_FOR_FIRSTNAME_LASTNAME = "Теперь вы \"%s\"\n/help";

    public static final String WHOAMI_ANSWER = "ФИО: %s";

    public static final String CREATETOPIC_ASK_FOR_TOPIC_NAME = "Пожалуйста, введите название домашнего задания (оно будет отображаться для сдачи):";
    public static final String CREATETOPIC_TOPIC_ILLEGAL_CHARACTERS = "Название содержит недопустимые символы\n(допустимые символы: буквы, цифры, пробел, \"_\", \"-\")";
    public static final String CREATETOPIC_TOPIC_TOO_MANY_CHARACTERS = "Название должно состоять из максимум 200 символов!";
    public static final String CREATETOPIC_TOPIC_ALREADY_EXIST = "Такое домашнее задание уже существует, введите другое:";
    public static final String CREATETOPIC_ASK_FOR_CONFIRMATION = "Вы уверены, что хотите добавить домашнее задание \"%s\"? (да/нет)";
    public static final String CREATETOPIC_CONFIRMATION_SUCCESS = "Новое домашнее задание добавлено в список\n/help";
    public static final String CREATETOPIC_CONFIRMATION_FAILURE = "Не удалось добавить новое домашнее задание\n/help";

    public static final String UPLOADSOLUTION_NO_AVAILABLE_TOPICS = "Нет доступных домашних заданий для сдачи";
    public static final String UPLOADSOLUTION_AVAILABLE_TOPICS_LIST = "Список доступных домашних заданий для сдачи:\n%s";
    public static final String UPLOADSOLUTION_ASK_FOR_TOPIC_NAME = "Пожалуйста, введите название домашнего задания, которое хотите сдать:";
    public static final String UPLOADSOLUTION_SUBMISSION_RULES = "Правила загрузки решения:\n1. Решение загружается одним файлом или по частям (конвертируется в pdf)\n2. Размер решения не должен превышать 5 мб\n3. Допустимые форматы: pdf или docx документ, jpeg или png фотография, текстовое сообщение (до 4096 символов)\nДля конвертации в pdf-документ можно воспользоваться бесплатным ресурсом [www.ilovepdf.com]";
    public static final String UPLOADSOLUTION_ASK_FOR_SUBMISSION = "Пожалуйста, загрузите ваше решение:\n(по окончании загрузки отправьте \"стоп-слово\")";
    public static final String UPLOADSOLUTION_EMPTY_SUBMISSION = "Пожалуйста, сначала загрузите ваше решение:";
    public static final String UPLOADSOLUTION_INCORRECT_COMBINATION = "Нельзя добавить файл этого формата к уже отправленным";
    public static final String UPLOADSOLUTION_LOADING_SUBMISSION = "Обрабатываю ваше решение...\n(это может занять некоторое время)";
    public static final String UPLOADSOLUTION_TOO_LARGE_MERGED_FILE = "Размер итогового файла превышает %s мб!\nПожалуйста, сожмите файлы и попробуйте снова:";
    public static final String UPLOADSOLUTION_TOO_LARGE_FILE = "Размер файла превышает %s мб!";
    public static final String UPLOADSOLUTION_ERROR_OCCURED = "При загрузке решения произошла ошибка (попробуйте еще раз позже)";
    public static final String UPLOADSOLUTION_ASK_FOR_CONFIRMATION = "Вы уверены, что хотите загрузить такое решение, как ответ на \"%s\"? (да/нет)";
    public static final String UPLOADSOLUTION_CONFIRMATION_SUCCESS = "Ваше решение успешно загружено\n/help";
    public static final String UPLOADSOLUTION_CONFIRMATION_FAILURE = "Не удалось загрузить решение\n/help";

    public static final String LISTUNSOLVED_UNSOVLED_TOPICS_LIST = "Список домашних заданий, ожидающих вашего решения:\n%s";
    public static final String LISTUNSOLVED_NO_UNSOLVED_TOPICS = "Вы решили все возможные домашние задания (партия довольна)\n/help";

    public static final String DUMPSOLUTIONS_NO_SUBMISSIONS = "Нет загруженных решений для выгрузки";
    public static final String DUMPSOLUTIONS_START_DUMP = "Начинаю выгрузку всех решений...\n(это может занять некоторое время)";
    public static final String DUMPSOLUTIONS_FINISH_DUMP = "Выгрузка решений успешно завершена";
    public static final String DUMPSOLUTIONS_ERROR_OCCURED = "При выгрузке решений произошла ошибка (попробуйте еще раз позже)";

    public static final String DUMPTOPIC_ASK_FOR_TOPIC_NAME = "Выберите домашнее задание для выгрузки:";
    public static final String DUMPTOPIC_NO_SUBMISSIONS = "Нет загруженных решений для выгрузки";
    public static final String DUMPTOPIC_START_DUMP = "Начинаю выгрузку решений...\n(это может занять некоторое время)";
    public static final String DUMPTOPIC_ERROR_OCCURED = "При выгрузке решений произошла ошибка (попробуйте еще раз позже)";

    public static final String REMOVETOPIC_ASK_FOR_TOPIC_NAME = "Выберите домашнее задание для удаления:";
    public static final String REMOVETOPIC_ASK_FOR_CONFIRMATION = "Вы уверены, что хотите удалить домашнее задание \"%s\"?\n(все отправленные решения будут безвозвратно удалены)";
    public static final String REMOVETOPIC_CONFIRMATION_SUCCESS = "Домашнее задание вместе с загруженными решениями успешно удалено\n/help";
    public static final String REMOVETOPIC_CONFIRMATION_FAILURE = "Не удалось удалить домашнее задание\n/help";

}
