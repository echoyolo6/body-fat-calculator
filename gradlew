#!/bin/sh

#
# Copyright © 2015 The Gradle Authors
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS="-Xmx64m -Xms64m"

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

warn () {
    echo "$*"
} >&2

die () {
    echo
    echo "$*"
    echo
    exit 1
} >&2

# OS specific support (must be 'true' or 'false').
cygwin=false
msys=false
darwin=false
case "$(uname)" in
    CYGWIN* )
        cygwin=true
        ;;
    Darwin* )
        darwin=true
        ;;
    MINGW* )
        msys=true
        ;;
esac

# For Cygwin or MSYS, switch paths to Windows format before running java
if $cygwin || $msys ; then
    APP_HOME_DIR=`cygpath --path --mixed "$APP_HOME_DIR"`
    CLASSPATH=`cygpath --path --mixed "$CLASSPATH"`
fi

# Split up the JVM_OPTS And GRADLE_OPTS values into an array, following the shell quoting and substitution rules
function splitJvmOpts() {
    JVM_OPTS=("$@")
}
eval splitJvmOpts $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS
JVM_OPTS[${#JVM_OPTS[*]}]="-Dorg.gradle.appname=$APP_BASE_NAME"

# Collect all arguments for the java command:
#   * Based on the shell quoting rules, a space-separated list of arguments is collected in "$@"
#   * The arguments are collected in an array honoring the shell quoting, substitution and escaping rules
function collectJavaArgs() {
    for arg in "$@" ; do
        if [[ -n "$arg" ]] ; then
            JAVA_OPTS[${#JAVA_OPTS[*]}]="$arg"
        fi
    done
}
eval collectJavaArgs $GRADLE_OPTS

# Use the -option file to avoid quoting issues on windows
# This option file contains all the parameters to be passed to the java command
# This is done by creating a temporary file in the temp directory with extension .opts
# The temporary file is deleted after the java command finishes
function createAppCommandFile() {
    # Create a temporary file
    APP_COMMAND_FILE=`mktemp "$TMPDIR/$APP_BASE_NAME".XXXXX`.opts
    # Add the main class and the original application arguments to the temporary file
    echo "$APP_MAIN_CLASS" "$@" > "$APP_COMMAND_FILE"
}
createAppCommandFile "$@"

# Change current working directory to the script directory
cd "$(dirname "$0")"

# Set up the command to execute
GRADLE_OPTS="-Dorg.gradle.wrapper.GradleWrapperMain"
exec java "${JVM_OPTS[@]}" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "@$APP_COMMAND_FILE"