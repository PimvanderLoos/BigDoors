#!/bin/bash

function install_dependency() {
    url="$1"
    name="$2"
    groupId="$3"
    artifactId="$4"
    version="$5"
    packaging="$6"

    tmp_name="$name-$version.jar"
    tmp_file="/tmp/$tmp_name"
    echo "Installing $tmp_name in local repository..."

    wget -O "$tmp_file" "$url"
    mvn install:install-file -Dfile="$tmp_file" -DgroupId="$groupId" -DartifactId="$artifactId" -Dversion="$version" -Dpackaging="$packaging"
}

version_medieval_factions="5.2.0"
install_dependency \
    "https://github.com/Dans-Plugins/Medieval-Factions/releases/download/v$version_medieval_factions/medieval-factions-$version_medieval_factions-all.jar" \
    "Medieval-Factions" \
    "com.dansplugins" \
    "medieval-factions" \
    "$version_medieval_factions" \
    "jar"




