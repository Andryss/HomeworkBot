package ru.andryss.homeworkbot.commands;

/**
 * Class containing all messages the bot can send
 */
public class Messages {

    private Messages() {
        throw new UnsupportedOperationException("util class");
    }

    // Dispatcher blok
    static final String DISPATCHER_NO_COMMAND = "К сожалению, я умею отвечать только на команды\n/help";
    static final String DISPATCHER_UNKNOWN_COMMAND = "Такой команды я не знаю\n/help";
    static final String DISPATCHER_ERROR = "При обработке команды произошла неизвестная ошибка, попробуйте еще раз позже";
    static final String DISPATCHER_HANDLER_ERROR = "Во время работы команды произошла неизвестная ошибка, попробуйте еще раз позже";

    // Common messages block
    static final String REGISTER_FIRST = "Для начала зарегистрируйтесь\n/start";
    static final String NOT_LEADER = "Вы не являетесь старостой";
    static final String TOPIC_NOT_FOUND = "Пожалуйста, введите название существующего домашнего задания:";
    static final String ASK_FOR_RESENDING_TOPIC = "Пожалуйста, введите название домашнего задания текстом:";
    static final String YES_ANSWER = "да";
    static final String NO_ANSWER = "нет";
    static final String ASK_FOR_RESENDING_CONFIRMATION = "Пожалуйста, выберите \"да\" или \"нет\":";
    static final String STOP_WORD = "стоп-слово";


    // Specific messages block (starts with "<COMMAND_NAME>_")
    static final String START_ASK_FOR_FIRSTNAME_LASTNAME = "Пожалуйста, введите ФИО, так будут подписаны ваши работы\n(например, \"Иванов Иван Иванович\"):";
    static final String START_ILLEGAL_CHARACTERS = "ФИО содержит недопустимые символы\n(допустимые символы: буквы, пробел, \"-\")";
    static final String START_TOO_MANY_CHARACTERS = "ФИО должно состоять из максимум 70 символов!";
    static final String START_ALREADY_REGISTERED = "Кто-то уже зарегистрировался под таким ФИО";
    static final String START_ANSWER_FOR_FIRSTNAME_LASTNAME = "Теперь вы \"%s\"\n/help";

    static final String WHOAMI_ANSWER = "ФИО: %s";

    static final String CREATETOPIC_ASK_FOR_TOPIC_NAME = "Пожалуйста, введите название домашнего задания (оно будет отображаться для сдачи):";
    static final String CREATETOPIC_TOPIC_ILLEGAL_CHARACTERS = "Название содержит недопустимые символы\n(допустимые символы: буквы, цифры, пробел, \"_\", \"-\")";
    static final String CREATETOPIC_TOPIC_TOO_MANY_CHARACTERS = "Название должно состоять из максимум 200 символов!";
    static final String CREATETOPIC_TOPIC_ALREADY_EXIST = "Такое домашнее задание уже существует, введите другое:";
    static final String CREATETOPIC_ASK_FOR_CONFIRMATION = "Вы уверены, что хотите добавить домашнее задание \"%s\"? (да/нет)";
    static final String CREATETOPIC_CONFIRMATION_SUCCESS = "Новое домашнее задание добавлено в список\n/help";
    static final String CREATETOPIC_CONFIRMATION_FAILURE = "Не удалось добавить новое домашнее задание\n/help";

    static final String LISTTOPICS_NO_TOPICS = "Нет домашних заданий";
    static final String LISTTOPICS_TOPICS_LIST = "Список домашних заданий: %s";

    static final String UPLOADSOLUTION_NO_AVAILABLE_TOPICS = "Нет доступных домашних заданий для сдачи";
    static final String UPLOADSOLUTION_AVAILABLE_TOPICS_LIST = "Список доступных домашних заданий для сдачи: %s";
    static final String UPLOADSOLUTION_ASK_FOR_TOPIC_NAME = "Пожалуйста, введите название домашнего задания, которое хотите сдать:";
    static final String UPLOADSOLUTION_SUBMISSION_RULES = "Правила загрузки решения:\n1. Решение загружается одним файлом или по частям (конвертируется в pdf)\n2. Размер решения не должен превышать 5 мб\n3. Допустимые форматы: pdf или docx документ, jpeg или png фотография, текстовое сообщение (до 4096 символов)\nДля конвертации в pdf-документ можете воспользоваться бесплатным ресурсом [www.ilovepdf.com]";
    static final String UPLOADSOLUTION_ASK_FOR_SUBMISSION = "Пожалуйста, загрузите ваше решение:\n(по окончании загрузки отправьте \"стоп-слово\")";
    static final String UPLOADSOLUTION_EMPTY_SUBMISSION = "Пожалуйста, сначала загрузите ваше решение:";
    static final String UPLOADSOLUTION_INCORRECT_COMBINATION = "Нельзя добавить файл этого формата к уже отправленным";
    static final String UPLOADSOLUTION_LOADING_SUBMISSION = "Обрабатываю ваше решение...\n(это может занять некоторое время)";
    static final String UPLOADSOLUTION_TOO_LARGE_MERGED_FILE = "Размер итогового файла превышает 5 мб!\nПожалуйста, сожмите файлы и попробуйте снова:";
    static final String UPLOADSOLUTION_TOO_LARGE_FILE = "Размер файла превышает 5 мб!";
    static final String UPLOADSOLUTION_ERROR_OCCURED = "При загрузке решения произошла ошибка (попробуйте еще раз позже)";
    static final String UPLOADSOLUTION_ASK_FOR_CONFIRMATION = "Вы уверены, что хотите загрузить такое решение, как ответ на \"%s\"? (да/нет)";
    static final String UPLOADSOLUTION_CONFIRMATION_SUCCESS = "Ваше решение успешно загружено\n/help";
    static final String UPLOADSOLUTION_CONFIRMATION_FAILURE = "Не удалось загрузить решение\n/help";

    static final String LISTUNSOLVED_UNSOVLED_TOPICS_LIST = "Список домашних заданий, ожидающих вашего решения: %s";
    static final String LISTUNSOLVED_NO_UNSOLVED_TOPICS = "Вы решили все возможные домашние задания (партия довольна)\n/help";

    static final String DUMPSOLUTIONS_NO_SUBMISSIONS = "Нет загруженных решений для выгрузки";
    static final String DUMPSOLUTIONS_START_DUMP = "Начинаю выгрузку решений...\n(это может занять некоторое время)";
    static final String DUMPSOLUTIONS_ERROR_OCCURED = "При выгрузке решений произошла ошибка (попробуйте еще раз позже)";

}
