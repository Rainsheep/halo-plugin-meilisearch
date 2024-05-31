#!/bin/sh

# 所本地不存在指定版本 meilisearch 的二进制文件，则远程下载 , 并且删除旧版本的文件

# GLOBALS

# Colors
RED='\033[31m'
GREEN='\033[32m'
DEFAULT='\033[0m'

# Project name
PNAME='meilisearch'

# GitHub Release address
GITHUB_REL='https://oss.rainsheep.cn/meilisearch'

LATEST=$1
SERVICE_PATH=$2

# Gets the version of the latest stable version of Meilisearch by setting the $latest variable.
# Returns 0 in case of success, 1 otherwise.
get_latest() {
    latest="$LATEST"
    echo "latest version: $latest"
    return 0
}

# Gets the OS by setting the $os variable.
# Returns 0 in case of success, 1 otherwise.
get_os() {
    os_name=$(uname -s)
    case "$os_name" in
    'Darwin')
        os='macos'
        ;;
    'Linux')
        os='linux'
        ;;
     'MINGW'*)
        os='windows'
        ;;
    *)
        return 1
    esac
    return 0
}

# Gets the architecture by setting the $archi variable.
# Returns 0 in case of success, 1 otherwise.
get_archi() {
    architecture=$(uname -m)
    case "$architecture" in
    'x86_64' | 'amd64' )
        archi='amd64'
        ;;
    'arm64')
        # macOS M1/M2
        if [ $os = 'macos' ]; then
            archi='apple-silicon'
        else
            archi='aarch64'
        fi
        ;;
    'aarch64')
        archi='aarch64'
        ;;
    *)
        return 1
    esac
    return 0
}

success_usage() {
    printf "$GREEN%s\n$DEFAULT" "Meilisearch $latest binary successfully downloaded as '$binary_name' file."
    echo ''
    echo 'Run it:'
    echo "    $ ./$PNAME"
    echo 'Usage:'
    echo "    $ ./$PNAME --help"
}

not_available_failure_usage() {
    printf "$RED%s\n$DEFAULT" 'ERROR: Meilisearch binary is not available for your OS distribution or your architecture yet.'
    echo ''
    echo 'However, you can easily compile the binary from the source files.'
    echo 'Follow the steps at the page ("Source" tab): https://www.meilisearch.com/docs/learn/getting_started/installation'
}

fetch_release_failure_usage() {
    echo ''
    printf "$RED%s\n$DEFAULT" 'ERROR: Impossible to get the latest stable version of Meilisearch.'
    echo 'Please let us know about this issue: https://github.com/meilisearch/meilisearch/issues/new/choose'
    echo ''
    echo 'In the meantime, you can manually download the appropriate binary from the GitHub release assets here: https://github.com/meilisearch/meilisearch/releases/latest'
}

fill_release_variables() {
    # Fill $latest variable.
    if ! get_latest; then
        fetch_release_failure_usage
        exit 1
    fi
    if [ "$latest" = '' ]; then
        fetch_release_failure_usage
        exit 1
     fi
     # Fill $os variable.
     if ! get_os; then
        not_available_failure_usage
        exit 1
     fi
     # Fill $archi variable.
     if ! get_archi; then
        not_available_failure_usage
        exit 1
     fi
}

# 删除所有旧文件夹
delete_old_files() {
    ls -d */ | while read folder; do
        # 获取文件夹的名称（不包括路径）
        folder_name=${folder%/}

        # 如果文件夹名称不等于 $latest，则删除该文件夹及其内容
        if [ "$folder_name" != "$latest" ]; then
            rm -rf "$folder"
            echo "delete old version meilisearch folder: $folder_name"
        fi
    done
}

download_binary() {
    fill_release_variables
    echo "Downloading Meilisearch binary $latest for $os, architecture $archi..."
    case "$os" in
        'windows')
            release_file="$PNAME-$os-$archi.exe"
            binary_name="$PNAME.exe"
            ;;
        *)
            release_file="$PNAME-$os-$archi"
            binary_name="$PNAME"
    esac
    cd $SERVICE_PATH
    delete_old_files
    mkdir $latest
    cd $latest
    file_url="$GITHUB_REL/$latest/$release_file"
    echo "meilisearch file remote url: $file_url"
    # 判断 $binary_name 文件的大小和 远程文件大小是否一致
    if [ -f "$binary_name" ]; then
        local_size=$(ls -l $binary_name | awk '{print $5}' | tr -d '\n\r')
        remote_size=$(curl -sI $file_url | grep -i Content-Length | awk '{print $2}' | tr -d '\n\r')
        echo "local_size: $local_size, remote_size: $remote_size"
        if [ "$local_size" = "$remote_size" ]; then
            echo "Meilisearch binary $latest for $os, architecture $archi already exists."
            success_usage
            return 0
        else
            echo "Delete the old file and download the new file."
            rm -f "$binary_name"
        fi
    fi

    # Fetch the Meilisearch binary.
    curl --fail -OL $file_url
    if [ $? -ne 0 ]; then
        fetch_release_failure_usage
        exit 1
    fi
    mv "$release_file" "$binary_name"
    chmod 744 "$binary_name"
    success_usage
}

# MAIN
main() {
    download_binary
}
main