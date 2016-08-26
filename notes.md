## Fix key host verification failure

```
sudo su - jenkins
ssh-keyscan -H github.com >> ~/.ssh/known_hosts
```

## Generate SSH Key for Jenkins

1. Log in with Jenkins user

`sudo su - jenkins`

2. Generate SSH Key

`ssh-keygen`

3. Let the default options

```
hydrogenium:~ jenkins$ ssh-keygen
Generating public/private rsa key pair.
Enter file in which to save the key (/Users/Shared/Jenkins/.ssh/id_rsa):
Enter passphrase (empty for no passphrase):
Enter same passphrase again:
```

For simplicity, no password was given

4. Copy the public key

`cat /Users/Shared/Jenkins/.ssh/id_rsa.pub`

5. Go to GitHub and add the public key as a deploy key

## Add the user and group jenkins on boot2docker VM (after each reboot)

```
sudo addgroup -g 1000 jenkins
sudo adduser -G jenkins -D -H -u 2000 jenkins

# And create the jenkins directory

sudo mkdir -p /jenkins/home
sudo chown jenkins:jenkins /jenkins
```

## Get the list of plugins with the versions to update the plugins.txt

```
JENKINS_HOST=host:port
curl -sSL "http://$JENKINS_HOST/pluginManager/api/xml?depth=1&xpath=/*/*/shortName|/*/*/version&wrapper=plugins" | perl -pe 's/.*?<shortName>([\w-]+).*?<version>([^<]+)()(<\/\w+>)+/\1 \2\n/g'|sed 's/ /:/'
```
