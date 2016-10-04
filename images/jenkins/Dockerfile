FROM jenkinsci/jenkins

USER root

RUN apt-get update ; apt-get install docker -y \
    && rm -rf /var/lib/apt/lists/*

COPY plugins.txt /usr/share/jenkins/plugins.txt

RUN /usr/local/bin/plugins.sh /usr/share/jenkins/plugins.txt

RUN echo 2.0 > /usr/share/jenkins/ref/jenkins.install.UpgradeWizard.state

COPY config /usr/share/jenkins/ref

COPY jobs /usr/share/jenkins/ref/jobs/

COPY scripts/wait-for-it.sh .
COPY scripts/start.sh .

RUN mkdir /envs

VOLUME /envs

ENTRYPOINT ["/bin/tini", "--", "./start.sh"]
