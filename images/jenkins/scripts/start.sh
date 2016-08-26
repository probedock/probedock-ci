#!/bin/bash -e

# This script will remove files that are already copied in the Jenkins home
# from the original Docker image starting script which handle the first setup.
# When Jenkins already have a filled home directory, the files contained
# it this directory will never be replaced again. That's the reason we have
# this script.

# Force the copy of the jobs files when the jenkins jobs directory already exists
if [[ -d /var/jenkins_home/jobs ]]; then
  rm -rf /var/jenkins_home/jobs/*
  cp -r /usr/share/jenkins/ref/jobs/* /var/jenkins_home/jobs/
fi

# Force the copy of main Jenkins configuration files
if [[ -f /var/jenkins_home/config.xml ]]; then
  cp /usr/share/jenkins/ref/config.xml /var/jenkins_home/config.xml
fi

if [[ -f /var/jenkins_home/scriptApproval.xml ]]; then
  cp /usr/share/jenkins/ref/scriptApproval.xml /var/jenkins_home/scriptApproval.xml
fi

/usr/local/bin/jenkins.sh
