package ru.andryss.homeworkbot.commands;

/**
 * Class containing all messages the bot can send
 */
public class Messages {

    private Messages() {
        throw new UnsupportedOperationException("util class");
    }

    // Command descriptions
    public static final String COMMAND_HELP = "${commands.help.description}";
    public static final String COMMAND_START = "${commands.start.description}";
    public static final String COMMAND_WHOAMI = "${commands.whoami.description}";
    public static final String COMMAND_CREATETOPIC = "${commands.createtopic.description}";
    public static final String COMMAND_REMOVETOPIC = "${commands.removetopic.description}";
    public static final String COMMAND_LISTTOPICS = "${commands.listtopics.description}";
    public static final String COMMAND_LISTUNSOLVED = "${commands.listunsolved.description}";
    public static final String COMMAND_UPLOADSOLUTION = "${commands.uploadsolution.description}";
    public static final String COMMAND_DUMPSOLUTIONS = "${commands.dumpsolutions.description}";
    public static final String COMMAND_DUMPTOPIC = "${commands.dumptopic.description}";

    // Dispatcher block
    public static final String DISPATCHER_NO_COMMAND = "${dispatcher.no_command}";
    public static final String DISPATCHER_UNKNOWN_COMMAND = "${dispatcher.unknown_command}";
    public static final String DISPATCHER_ERROR = "${dispatcher.internal_error}";
    public static final String DISPATCHER_HANDLER_ERROR = "${dispatcher.handler_error}";

    // Common messages block
    public static final String REGISTER_FIRST = "${common.register_first}";
    public static final String NOT_LEADER = "${common.not_leader}";
    public static final String NO_TOPICS = "${common.no_topics}";
    public static final String TOPICS_LIST = "${common.topics_list}";
    public static final String TOPIC_NOT_FOUND = "${common.topic_not_found}";
    public static final String ASK_FOR_RESENDING_TOPIC = "${common.ask_for_resending_topic}";
    public static final String YES_ANSWER = "${common.yes_answer}";
    public static final String NO_ANSWER = "${common.no_answer}";
    public static final String ASK_FOR_RESENDING_CONFIRMATION = "${common.ask_for_resending_confirmation}";
    public static final String STOP_WORD = "${common.stop_word}";


    // Specific messages block (starts with "<COMMAND_NAME>_")
    public static final String START_ASK_FOR_FIRSTNAME_LASTNAME = "${commands.start.ask_for_firstname_lastname}";
    public static final String START_ILLEGAL_CHARACTERS = "${commands.start.illegal_characters}";
    public static final String START_TOO_MANY_CHARACTERS = "${commands.start.too_many_characters}";
    public static final String START_ALREADY_REGISTERED = "${commands.start.already_registered}";
    public static final String START_ANSWER_FOR_FIRSTNAME_LASTNAME = "${commands.start.answer_for_firstname_lastname}";

    public static final String WHOAMI_ANSWER = "${commands.whoami.answer}";

    public static final String CREATETOPIC_ASK_FOR_TOPIC_NAME = "${commands.createtopic.ask_for_topic_name}";
    public static final String CREATETOPIC_TOPIC_ILLEGAL_CHARACTERS = "${commands.createtopic.topic_illegal_characters}";
    public static final String CREATETOPIC_TOPIC_TOO_MANY_CHARACTERS = "${commands.createtopic.topic_too_many_characters}";
    public static final String CREATETOPIC_TOPIC_ALREADY_EXIST = "${commands.createtopic.topic_already_exist}";
    public static final String CREATETOPIC_ASK_FOR_CONFIRMATION = "${commands.createtopic.ask_for_confirmation}";
    public static final String CREATETOPIC_CONFIRMATION_SUCCESS = "${commands.createtopic.confirmation_success}";
    public static final String CREATETOPIC_CONFIRMATION_FAILURE = "${commands.createtopic.confirmation_failure}";

    public static final String UPLOADSOLUTION_NO_AVAILABLE_TOPICS = "${commands.uploadsolution.no_available_topics}";
    public static final String UPLOADSOLUTION_AVAILABLE_TOPICS_LIST = "${commands.uploadsolution.available_topics_list}";
    public static final String UPLOADSOLUTION_ASK_FOR_TOPIC_NAME = "${commands.uploadsolution.ask_for_topic_name}";
    public static final String UPLOADSOLUTION_SUBMISSION_RULES = "${commands.uploadsolution.submission_rules}";
    public static final String UPLOADSOLUTION_ASK_FOR_SUBMISSION = "${commands.uploadsolution.ask_for_submission}";
    public static final String UPLOADSOLUTION_EMPTY_SUBMISSION = "${commands.uploadsolution.empty_submission}";
    public static final String UPLOADSOLUTION_INCORRECT_COMBINATION = "${commands.uploadsolution.incorrect_combination}";
    public static final String UPLOADSOLUTION_LOADING_SUBMISSION = "${commands.uploadsolution.loading_submission}";
    public static final String UPLOADSOLUTION_TOO_LARGE_MERGED_FILE = "${commands.uploadsolution.too_large_merged_file}";
    public static final String UPLOADSOLUTION_TOO_LARGE_FILE = "${commands.uploadsolution.too_large_file}";
    public static final String UPLOADSOLUTION_ERROR_OCCURED = "${commands.uploadsolution.error_occured}";
    public static final String UPLOADSOLUTION_ASK_FOR_CONFIRMATION = "${commands.uploadsolution.ask_for_confirmation}";
    public static final String UPLOADSOLUTION_CONFIRMATION_SUCCESS = "${commands.uploadsolution.confirmation_success}";
    public static final String UPLOADSOLUTION_CONFIRMATION_FAILURE = "${commands.uploadsolution.confirmation_failure}";

    public static final String LISTUNSOLVED_UNSOVLED_TOPICS_LIST = "${commands.listunsolved.unsovled_topics_list}";
    public static final String LISTUNSOLVED_NO_UNSOLVED_TOPICS = "${commands.listunsolved.no_unsolved_topics}";

    public static final String DUMPSOLUTIONS_NO_SUBMISSIONS = "${commands.dumpsolutions.no_submissions}";
    public static final String DUMPSOLUTIONS_START_DUMP = "${commands.dumpsolutions.start_dump}";
    public static final String DUMPSOLUTIONS_FINISH_DUMP = "${commands.dumpsolutions.finish_dump}";
    public static final String DUMPSOLUTIONS_ERROR_OCCURED = "${commands.dumpsolutions.error_occured}";

    public static final String DUMPTOPIC_ASK_FOR_TOPIC_NAME = "${commands.dumptopic.ask_for_topic_name}";
    public static final String DUMPTOPIC_NO_SUBMISSIONS = "${commands.dumptopic.no_submissions}";
    public static final String DUMPTOPIC_START_DUMP = "${commands.dumptopic.start_dump}";
    public static final String DUMPTOPIC_ERROR_OCCURED = "${commands.dumptopic.error_occured}";

    public static final String REMOVETOPIC_ASK_FOR_TOPIC_NAME = "${commands.removetopic.ask_for_topic_name}";
    public static final String REMOVETOPIC_ASK_FOR_CONFIRMATION = "${commands.removetopic.ask_for_confirmation}";
    public static final String REMOVETOPIC_CONFIRMATION_SUCCESS = "${commands.removetopic.confirmation_success}";
    public static final String REMOVETOPIC_CONFIRMATION_FAILURE = "${commands.removetopic.confirmation_failure}";

}
