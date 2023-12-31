package ru.andryss.homeworkbot.commands;

public class Messages {

    // Common messages block
    static final String REGISTER_FIRST = "Для начала зарегистрируйтесь\n/start";
    static final String NOT_LEADER = "Вы не являетесь старостой";
    static final String ASK_FOR_RESENDING_TOPIC = "Пожалуйста, введите название домашнего задания текстом:";
    static final String YES_ANSWER = "да";
    static final String NO_ANSWER = "нет";
    static final String ASK_FOR_RESENDING_CONFIRMATION = "Пожалуйста, выберите \"да\" или \"нет\":";


    // Specific messages block (starts with "<COMMAND_NAME>_")
    static final String START_ASK_FOR_FIRSTNAME_LASTNAME = "Пожалуйста, введите ФИО, так будут подписаны ваши работы\n(например, \"Иванов Иван Иванович\"):";
    static final String START_ALREADY_REGISTERED = "Кто-то уже зарегистрировался под таким ФИО";
    static final String START_ANSWER_FOR_FIRSTNAME_LASTNAME = "Теперь вы \"%s\"\n/help";

    static final String WHOAMI_ANSWER = "ФИО: %s";

    static final String CREATETOPIC_ASK_FOR_TOPIC_NAME = "Пожалуйста, введите название домашнего задания (оно будет отображаться для сдачи):";
    static final String CREATETOPIC_TOPIC_ALREADY_EXIST = "Такое домашнее задание уже существует, введите другое:";
    static final String CREATETOPIC_ASK_FOR_CONFIRMATION = "Вы уверены, что хотите добавить домашнее задание \"%s\"? (да/нет)";
    static final String CREATETOPIC_CONFIRMATION_SUCCESS = "Новое домашнее задание добавлено в список\n/help";
    static final String CREATETOPIC_CONFIRMATION_FAILURE = "Не удалось добавить новое домашнее задание\n/help";

    static final String LISTTOPICS_NO_TOPICS = "Нет домашних заданий";
    static final String LISTTOPICS_TOPICS_LIST = "Список домашних заданий: %s";

    static final String UPLOADSOLUTION_NO_AVAILABLE_TOPICS = "Нет доступных домашних заданий для сдачи";
    static final String UPLOADSOLUTION_AVAILABLE_TOPICS_LIST = "Список доступных домашних заданий для сдачи: %s";
    static final String UPLOADSOLUTION_ASK_FOR_TOPIC_NAME = "Пожалуйста, введите название домашнего задания, которое хотите сдать:";
    static final String UPLOADSOLUTION_SUBMISSION_RULES = "Правила загрузки решения:\n1. Размер решения не должен привышать 10 мб\n2. Допустимые форматы: pdf,doc документ, jpeg или png картинка (можно приложить, как фото или документ), текстовое сообщение\n3. Группа фотографий НЕ поддерживается. Для конвертации в pdf-документ можете воспользоваться [www.ilovepdf.com]";
    static final String UPLOADSOLUTION_ASK_FOR_SUBMISSION = "Пожалуйста, загрузите ваше решение:";
    static final String UPLOADSOLUTION_ASK_FOR_RESENDING_SUBMISSION = "Пожалуйста, загрузите ваше решение в виде документа:";
    static final String UPLOADSOLUTION_ERROR_OCCURED = "При загрузке решения произошла ошибка (попробуйте еще раз позже)";
    static final String UPLOADSOLUTION_ASK_FOR_CONFIRMATION = "Вы уверены, что хотите загрузить такое решение, как ответ на \"%s\"? (да/нет)";
    static final String UPLOADSOLUTION_CONFIRMATION_SUCCESS = "Ваше решение успешно загружено\n/help";
    static final String UPLOADSOLUTION_CONFIRMATION_FAILURE = "Не удалось загрузить решение\n/help";

    static final String LISTUNSOLVED_UNSOVLED_TOPICS_LIST = "Список домашних заданий, ожидающих вашего решения: %s";
    static final String LISTUNSOLVED_NO_UNSOLVED_TOPICS = "Вы решили все возможные домашние задания (партия довольна)\n/help";

    static final String DUMPSOLUTIONS_NO_SUBMISSIONS = "Нет загруженных решений для выгрузки";
    static final String DUMPSOLUTIONS_START_DUMP = "Начинаю выгрузку решений... \n(это может занять некоторое время)";
    static final String DUMPSOLUTIONS_ERROR_OCCURED = "При выгрузке решений произошла ошибка (попробуйте еще раз позже)";

}
