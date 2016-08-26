import jenkins.model.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import org.jenkinsci.plugins.plaincredentials.*
import org.jenkinsci.plugins.plaincredentials.impl.*
import hudson.util.Secret
import hudson.plugins.sshslaves.*
import java.lang.StringBuilder

/**
 * Workaround join method to avoid rejection exception of unclassified method java.util.ArrayList join
 */
def join(List lst) {
    def sb = new StringBuilder()
    for (String s : lst) {
        sb.append(s)
    }
    return sb.toString()
}

/**
 * Generate a random string base on an alphabet and the number wanted
 */
def strGenerator(String alphabet, int n) {
    def rnd = new Random()
    def sb = new StringBuilder()

    for (int i = 0; i < n; i++) {
        sb.append(alphabet[rnd.nextInt(alphabet.length())])
    }

    return sb.toString()
}

def executeJob() {
    def Launcher = load 'ci/pipelines/src/utils/Launcher.groovy'
    def Passwords = load 'ci/pipelines/src/utils/Passwords.groovy'

    /**
     * Load the file that contains the next free RP port
     */
    def File rpPortFile = new File('/envs/.rpPort')

    /**
     * Load the property file that contains the port
     */
    def Properties rpProperties = new Properties()
    if (rpPortFile.exists()) {
        rpProperties.load(new FileInputStream(rpPortFile))
    } else {
        rpProperties.setProperty('PROBEDOCK_DOCKER_WEB_CONTAINER_PORT', '3000')
    }

    /**
     * The property file to handle each environment configuration
     */
    def File envFile = new File('/envs/' + env.PROBEDOCK_ENV)
    def envExists = envFile.exists()

    /**
     * Load the properties from the env file
     */
    def Properties envProperties = new Properties()
    if (envExists) {
        envProperties.load(new FileInputStream(envFile))
    }

    /**
     * This step will ask the Probe Dock deploy for several passwords that will be used to setup the database and such things.
     *
     * All the passwords will be stored through the Credentials plugin in a secure way.
     */
    stage 'Create a new Probe Dock environment'

    /**
     * Password generation rules
     */
    def passwordAlphabet = join(('A'..'Z') + ('a'..'z') + ('0'..'9'))
    def keysAlphabet = join(('A'..'Z') + ('a'..'z') + ('0'..'9'))
    def passwordLength = 32
    def keysLength = 128

    /**
     * Define the parameters that will be asked to the user
     */
    def parametersDefinitions = [[
        name       : 'PROBEDOCK_ENV',
        humanName  : 'Environment name',
        description: 'The environment name.',
        default    : env.PROBEDOCK_ENV,
        save       : false
    ], [
        name       : 'PROBEDOCK_DATA_PATH',
        humanName  : 'Data path',
        description: 'Host path to mount the volume for the Probe Dock data (Postgres data, ...). Each environment will create a subdirectory in this path.',
        default    : envExists ? envProperties.getProperty('PROBEDOCK_DATA_PATH') : '/data'
    ], [
        name       : 'PROBEDOCK_LOG_LEVEL',
        humanName  : 'Log level',
        description: 'Rails application log level.',
        default    : envExists ? envProperties.getProperty('PROBEDOCK_LOG_LEVEL') : 'info'
    ], [
        name       : 'PROBEDOCK_MAIL_ADDRESS',
        humanName  : 'SMTP server host',
        description: 'SMTP address to send e-mails.',
        default    : envExists ? envProperties.getProperty('PROBEDOCK_MAIL_ADDRESS') : ''
    ], [
        name       : 'PROBEDOCK_MAIL_PORT',
        humanName  : 'SMTP server port',
        description: 'SMTP port to send e-mails.',
        default    : envExists ? envProperties.getProperty('PROBEDOCK_MAIL_PORT') : '587'
    ], [
        name       : 'PROBEDOCK_MAIL_DOMAIN',
        humanName  : 'SMTP domain',
        description: 'SMTP domain to send e-mails. (Used in EHLO SMTP command).',
        default    : envExists ? envProperties.getProperty('PROBEDOCK_MAIL_DOMAIN') : ''
    ], [
        name       : 'PROBEDOCK_MAIL_AUTHENTICATION',
        humanName  : 'SMTP authentication',
        description: 'SMTP authentication method.',
        default    : envExists ? envProperties.getProperty('PROBEDOCK_MAIL_AUTHENTICATION') : 'plain'
    ], [
        name       : 'PROBEDOCK_MAIL_FROM',
        humanName  : 'SMTP sender address',
        description: 'From address for e-mails sent by Probe Dock.',
        default    : envExists ? envProperties.getProperty('PROBEDOCK_MAIL_FROM') : ''
    ], [
        name       : 'PROBEDOCK_MAIL_FROM_NAME',
        humanName  : 'SMTP sender name',
        description: 'From address name for e-mails sent by Probe Dock.',
        default    : envExists ? envProperties.getProperty('PROBEDOCK_MAIL_FROM_NAME') : ''
    ], [
        name       : 'PROBEDOCK_APP_PROTOCOL',
        humanName  : 'Application protocol',
        description: 'External address protocol (http or https).',
        choices    : envExists && envProperties.getProperty('PROBEDOCK_APP_PROTOCOL') == 'http' ? 'http\nhttps' : 'https\nhttp'
    ], [
        name       : 'PROBEDOCK_APP_HOST',
        humanName  : 'Application host',
        description: 'External address host (e.g. app.example.com)',
        default    : envExists ? envProperties.getProperty('PROBEDOCK_APP_HOST') : ''
    ], [
        name       : 'PROBEDOCK_APP_PORT',
        humanName  : 'Application port',
        description: 'External address port (e.g. 80, 443).',
        default    : envExists ? envProperties.getProperty('PROBEDOCK_APP_PORT') : '443'
    ], [
        name       : 'PROBEDOCK_UNICORN_WORKERS',
        humanName  : 'Number of unicorn workers',
        description: 'Number of Unicorn workers (Rails application instances) to run per application container.',
        default    : envExists ? envProperties.getProperty('PROBEDOCK_UNICORN_WORKERS') : '3'
    ], [
        name       : 'PROBEDOCK_DOCKER_APP_CONTAINERS',
        humanName  : 'Number of application containers',
        description: 'Number of application containers to run. Note that each application container might itself run multiple workers depending on PROBEDOCK_UNICORN_WORKERS.',
        default    : envExists ? envProperties.getProperty('PROBEDOCK_DOCKER_APP_CONTAINERS') : '3'
    ], [
        name       : 'PROBEDOCK_DOCKER_JOB_CONTAINERS',
        humanName  : 'Number of job containers',
        description: 'Number of background job containers to run.',
        default    : envExists ? envProperties.getProperty('PROBEDOCK_DOCKER_JOB_CONTAINERS') : '3'
    ], [
        name       : 'PROBEDOCK_DOCKER_WEB_CONTAINER_PORT',
        humanName  : 'Docker web container port',
        description: 'Host port to expose the web container on. Must be different for each environment. It will be used for port mapping.',
        default    : envExists ? envProperties.getProperty('PROBEDOCK_DOCKER_WEB_CONTAINER_PORT') : rpProperties.getProperty('PROBEDOCK_DOCKER_WEB_CONTAINER_PORT')
    ]]

    /**
     * Define the password definitions. They will be handled through the Credentials plugin and
     * then be stored securely.
     */
    def passwordDefinitions = [[
        name       : Passwords.PROBEDOCK_SMTP_USER_BASE_NAME,
        humanName  : 'SMTP user name',
        description: 'The SMTP user used to send emails from Probe Dock',
        default    : ''
    ], [
        name       : Passwords.PROBEDOCK_SMTP_PASSWORD_BASE_NAME,
        humanName  : 'SMTP Password',
        description: 'The SMTP password',
        default    : ''
    ], [
        name       : Passwords.POSTGRESSQL_PASSWORD_BASE_NAME,
        humanName  : 'PostgreSQL root password',
        description: 'The root password for PostgreSQL',
        default    : strGenerator(passwordAlphabet, passwordLength)
    ], [
        name       : Passwords.PROBEDOCK_DB_PASSWORD_BASE_NAME,
        humanName  : 'Probe Dock database password',
        description: 'The password for Probe Dock PostgreSQL database.',
        default    : strGenerator(passwordAlphabet, passwordLength)
    ], [
        name       : Passwords.PROBEDOCK_SECRET_KEY_BASE_NAME,
        humanName  : 'Secret key',
        description: 'The secret key base',
        default    : strGenerator(keysAlphabet, keysLength)
    ], [
        name       : Passwords.PROBEDOCK_JWT_SECRET_BASE_NAME,
        humanName  : 'JSON Web Token secret',
        description: 'The JWT secret',
        default    : strGenerator(keysAlphabet, keysLength)
    ]]

    /**
     * Enrich the parameter definitions by the passwords
     */
    if (!envExists) {
        for (int i = 0; i < passwordDefinitions.size(); i++) {
            // To make the difference between standard parameter and password parameter
            passwordDefinitions[i].password = true
            parametersDefinitions.add(passwordDefinitions[i])
        }
    }

    /**
     * Add a last parameter to decide if we start the first deployment right now or not
     */
    if (!envExists) {
        parametersDefinitions.add([
            name       : 'FIRST_DEPLOY',
            humanName  : 'Do the first deployment?',
            description: 'Perform the first deployment right after the configuration has been validated.',
            default    : false,
            boolean    : true,
            save       : false
        ])
        parametersDefinitions.add([
            name       : 'PROBEDOCK_ADMIN_USERNAME',
            humanName  : 'Probe Dock administrator user name',
            description: 'The Probe Dock administrator user name.',
            default    : 'admin',
            save       : false
        ])
        parametersDefinitions.add([
            name       : 'PROBEDOCK_ADMIN_PASSWORD',
            humanName  : 'Probe Dock admistrator password',
            description: 'The Probe Dock administrator password.',
            default    : '',
            save       : false
        ])
        parametersDefinitions.add([
            name       : 'PROBEDOCK_ADMIN_EMAIL',
            humanName  : 'Probe Dock administrator email',
            description: 'The Probe Dock administrator email.',
            default    : '',
            save       : false
        ])
    }

    /**
     * Build real map of parameters that is used by the DSL input method
     */
    def inputParameters = []
    for (int i = 0; i < parametersDefinitions.size(); i++) {
        // Check for choices parameter
        if (parametersDefinitions[i].containsKey('choices')) {
            inputParameters.add([
                $class     : 'hudson.model.ChoiceParameterDefinition',
                choices    : parametersDefinitions[i].choices,
                description: parametersDefinitions[i].description,
                name       : parametersDefinitions[i].humanName
            ])
        }

        // Check for boolean parameter
        else if (parametersDefinitions[i].containsKey('boolean')) {
            inputParameters.add([
                $class      : 'hudson.model.BooleanParameterDefinition',
                defaultValue: parametersDefinitions[i].default,
                description : parametersDefinitions[i].description,
                name        : parametersDefinitions[i].humanName
            ])
        }

        // Defaulting to string parameter
        else {
            inputParameters.add([
                $class      : 'hudson.model.StringParameterDefinition',
                defaultValue: parametersDefinitions[i].default,
                description : parametersDefinitions[i].description,
                name        : parametersDefinitions[i].humanName
            ])
        }
    }

    /**
     * Ask user for the parameters and passwords
     */
    def filledParameters = input(
        message:
            !envExists ?
                'Setup your environment.\n\n!! ATTENTION !!: You MUST store the credentials information in a secure way.' :
                'Update your environment configuration',
        parameters: inputParameters
    )

    /**
     * The storage of the password must be done after the passwords input retrieval to avoid serialization issue. In fact,
     * the SystemCredentialsProvider$StoreImpl is not serializable
     */
    def store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

    // Keep these lines of code to replace the global storage of password by dedicated storage by environment
    // domain = new Domain(PROBEDOCK_ENV, 'The credentials for the probe dock ' + PROBEDOCK_ENV + ' environment.', Collections.<DomainSpecification>emptyList())
    // store.addDomain(domain)

    // Replace this line by the two above once the Groovy sandboxing will allow to use SystemCredentialsProvider$StoreImpl.addDomain
    def domain = Domain.global()

    /**
     * Define the current runtime variables
     */
    def currentVars = [:]

    StringBuilder sb = new StringBuilder()
    // Store each passwords
    for (int i = 0; i < parametersDefinitions.size(); i++) {
        // Check if the parameters must be stored as a password
        if (parametersDefinitions[i].containsKey('password')) {
            def result = store.addCredentials(
                domain,
                new StringCredentialsImpl(
                    CredentialsScope.GLOBAL,
                    env.PROBEDOCK_ENV + '-' + parametersDefinitions[i].name,
                    '[' + env.PROBEDOCK_ENV + '] ' + parametersDefinitions[i].description,
                    Secret.fromString(filledParameters[parametersDefinitions[i].humanName])
                )
            )

            if (result) {
                println 'The password ' + parametersDefinitions[i].humanName + ' was successfully created.'
            } else {
                println 'The password ' + parametersDefinitions[i].humanName + ' already exists and will not be updated'
            }
        }

        // When we have a parameter that we not save into the property file, we just set it as env variables
        else if (parametersDefinitions[i].containsKey('save') && !parametersDefinitions[i].save) {
            env[parametersDefinitions[i].name] = filledParameters[parametersDefinitions[i].humanName]
        }

        // For all other parameters, we save them to a property file
        else {
            sb.append(parametersDefinitions[i].name).append('=').append(filledParameters[parametersDefinitions[i].humanName]).append('\n')
        }
    }

    /**
     * Save the content of the property file
     */
    envFile.write sb.toString()

    /**
     * We will make sure the RP property file is updated only when a new environment is created
     */
    if (!envExists) {
        /**
         * Build the RP property file content.
         * The TCP ports allocated for the RP will be 3000 and following.
         */
        StringBuilder rpSb = new StringBuilder();
        rpSb
            .append('PROBEDOCK_DOCKER_WEB_CONTAINER_PORT')
            .append('=')
            .append(Integer.parseInt(rpProperties.getProperty('PROBEDOCK_DOCKER_WEB_CONTAINER_PORT')) + 1)

        /**
         * Write the content of the file
         */
        rpPortFile.write rpSb.toString()
    }

    // Make sure the following variables will not be serialized for the next step which will fail due to store that is not serializable
    store = null
    domain = null

    stage 'Preform the first deployment'
    if (envExists) {
        println 'The environment configuration file already exists. The first deploy cannot be triggered.'
    } else {
        println 'The environment configuration file was created.'
        if (Boolean.parseBoolean(env.FIRST_DEPLOY)) {
            println 'The first deploy will now be triggered.'

            Launcher.launchJob(Launcher.JOB_FIRST_DEPLOY, true)
        }
    }
}

return this