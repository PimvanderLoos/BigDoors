#!/bin/bash

# Update these if needed!
J_8="/usr/lib/jvm/java-8-graalvm/bin/java"
J_17="/usr/lib/jvm/java-17-graalvm/bin/java"
BUILD_DIR="tmp"

VERSIONS=( 1.20 3798 1.20.1 )
JAVA_VERSIONS=( "$J_17" "$J_17" "$J_17" )

function setup_version_dir() {
    version="$1"
    dir="$2"
    java_path="$3"
    build_tools="$4"

    [[ -d "$dir" ]] || mkdir "$dir"
    echo "\"$java_path\" -jar \"$build_tools\" --rev \"$version\" --generate-docs --generate-source" > "$dir/run.sh"
}

function build_version() {
    version="$1"
    java_path="$2"
    build_tools="$3"

    dir="$BUILD_DIR/Spigot$version"

    setup_version_dir "$version" "$dir" "$java_path" "$build_tools"

    (cd "$dir" && bash "run.sh" &>/dev/null)
    result="$?"

    test "$result" -eq 0 && echo "Finished building $version" || echo "Failed building version $version"
}

function set_thread_count() {
    num_threads=$(grep -c ^processor /proc/cpuinfo)

    # Each build process will take up to 5GB, so that might be more limiting than number of cores
    # We definitely do _not_ want to swap!
    free_ram=$(awk '/MemFree/ { printf "%d \n", $2/1024/1024 }' /proc/meminfo)
    ram_limit=$((free_ram / 5))
    num_threads=$((num_threads < ram_limit ? num_threads : ram_limit))
    num_threads=$((num_threads < 1 ? 1 : num_threads))
}



[[ -f "$J_8" ]] || { echo "Could not find a Java 8 install at '$J_8'! Please update the path!"; exit 1;}
[[ -f "$J_17" ]] || { echo "Could not find a Java 17 install at '$J_17'! Please update the path!"; exit 1;}

[[ -d "$BUILD_DIR" ]] || mkdir -p "$BUILD_DIR"


export -f build_version
export -f setup_version_dir
export BUILD_DIR="$BUILD_DIR"


echo "Downloading BuildTools..."
wget https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar -qO "$BUILD_DIR/BuildTools.jar" >/dev/null
build_tools=$(readlink -f "$BUILD_DIR/BuildTools.jar")
printf "Done!\n\n"

set_thread_count
printf "Thread_count: %d\n\n" "$num_threads"


for i in "${!VERSIONS[@]}"; do
    version="${VERSIONS[i]}"
    java_path="${JAVA_VERSIONS[i]}"
    echo "Building: $version"
    sem -j $num_threads "build_version $version \"$java_path\" \"$build_tools\" --remap --remapped"
done
sem --wait

./installDependencies.sh "$BUILD_DIR"

echo "The environment has been set up! Feel free to remove the $BUILD_DIR directory now!"
