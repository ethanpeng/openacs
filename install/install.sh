#!/bin/bash

if [ -f /etc/redhat-release ]; then
    # RedHat
    ./install_fedora.sh
elif [ -f /etc/debian_version ]; then
    # Debian
    ./install_ubuntu.sh
fi
