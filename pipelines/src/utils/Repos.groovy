/**
 * Clone the CI repo
 */
def cloneCi() {
    checkout(
        changelog: false,
        poll: false,
        scm: [
            $class: 'GitSCM',
            branches: [[name: env.PROBEDOCK_CI_VERSION ? env.PROBEDOCK_CI_VERSION : '*/master']],
            doGenerateSubmoduleConfigurations: false,
            extensions: [
                [$class: 'RelativeTargetDirectory', relativeTargetDir: 'ci' ],
                [$class: 'WipeWorkspace']
            ],
            submoduleCfg: [],
            userRemoteConfigs: [[
                refspec: env.PROBEDOCK_CI_VERSION ? env.PROBEDOCK_CI_VERSION : 'master',
                url: env.REPO_CI
            ]]
        ]
    )
}

/**
 * Clone the Probe Dock repo
 */
def cloneProbeDock(path = null) {
    checkout(
        changelog: false,
        poll: false,
        scm: [
            $class: 'GitSCM',
            branches: [[name: env.PROBEDOCK_VERSION ? env.PROBEDOCK_VERSION : '*/master']],
            doGenerateSubmoduleConfigurations: false,
            extensions: [
                [$class: 'RelativeTargetDirectory', relativeTargetDir: path ? path : 'probedock' ],
                [$class: 'WipeWorkspace']
            ],
            submoduleCfg: [],
            userRemoteConfigs: [[
                refspec: env.PROBEDOCK_VERSION ? env.PROBEDOCK_VERSION : 'master',
                url: env.REPO_PROBEDOCK]
            ]
        ]
    )
}

///**
// * Clone all the repos
// */
//def cloneRepos() {
//    cloneCi()
//    cloneProbeDock()
//}

return this