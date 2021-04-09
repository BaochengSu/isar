# Minimal host Debian root file system
#
# This software is a part of ISAR.
# Copyright (c) Siemens AG, 2018
#
# SPDX-License-Identifier: MIT

Description = "Minimal host Debian root file system"

DEPLOY_ISAR_BOOTSTRAP = "${DEPLOY_DIR_BOOTSTRAP}/${HOST_DISTRO}-host_${DISTRO}-${DISTRO_ARCH}"

require isar-bootstrap.inc

HOST_DISTRO_BOOTSTRAP_KEYS ?= ""
DISTRO_BOOTSTRAP_KEYS = "${HOST_DISTRO_BOOTSTRAP_KEYS}"

do_apt_config_prepare[dirs] = "${WORKDIR}"
do_apt_config_prepare[vardeps] += "\
                                   APTPREFS \
                                   HOST_DISTRO_APT_PREFERENCES \
                                   APTCONFS \
                                   HOST_DISTRO_APT_CONFS \
                                   DEBDISTRONAME \
                                   APTSRCS \
                                   HOST_DISTRO_APT_SOURCES \
                                   DEPLOY_ISAR_BOOTSTRAP \
                                  "
python do_apt_config_prepare() {
    apt_preferences_out = d.getVar("APTPREFS", True)
    apt_preferences_list = (
        d.getVar("HOST_DISTRO_APT_PREFERENCES", True) or ""
    ).split()
    aggregate_files(d, apt_preferences_list, apt_preferences_out)

    apt_confs_out = d.getVar("APTCONFS", True)
    apt_confs_list = (
        d.getVar("HOST_DISTRO_APT_CONFS", True) or ""
    ).split()
    aggregate_files(d, apt_confs_list, apt_confs_out)


    apt_sources_out = d.getVar("APTSRCS", True)
    apt_sources_init_out = d.getVar("APTSRCS_INIT", True)
    apt_sources_list = (
        d.getVar("HOST_DISTRO_APT_SOURCES", True) or ""
    ).split()

    aggregate_files(d, apt_sources_list, apt_sources_init_out)
    aggregate_aptsources_list(d, apt_sources_list, apt_sources_out)
}
addtask apt_config_prepare before do_bootstrap after do_unpack

OVERRIDES_append = ":${@get_distro_needs_https_support(d, True)}"

do_bootstrap[vardeps] += "HOST_DISTRO_APT_SOURCES"
do_bootstrap() {
    isar_bootstrap --host
}
addtask bootstrap before do_build after do_generate_keyrings
