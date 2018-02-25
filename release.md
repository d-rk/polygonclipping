# Release Process

## start release
mvn gitflow:release-start -Prelease

## finish release
mvn gitflow:release-finish -Prelease

## deploy to sonatype
git checkout master
mvn deploy -Prelease

## sync repository to maven central
* Login at https://oss.sonatype.org
* Goto `Build Promotion`, `Staging Repositories`
* Select `comgithubrandom-dwi-*` and hit `close`
* when closed successfully, hit `release`