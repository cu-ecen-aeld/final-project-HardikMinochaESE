# See https://git.yoctoproject.org/poky/tree/meta/files/common-licenses
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# TODO: Set this  with the path to your assignments rep.  Use ssh protocol and see lecture notes
# about how to setup ssh-agent for passwordless access

SRC_URI = "git://github.com/HardikMinochaESE/i2c-lcd-driver-final-project.git;protocol=https;branch=main"

PV = "1.0+git${SRCPV}"
# TODO: set to reference a specific commit hash in your assignment repo
#SRCREV = "ddd8afd72fe23de049c73cd53efcd01818390e4e"
SRCREV = "${AUTOREV}"

# This sets your staging directory based on WORKDIR, where WORKDIR is defined at 
# https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-WORKDIR
# We reference the "server" directory here to build from the "server" directory
# in your assignments repo
S = "${WORKDIR}/git/lcd-driver"

# TODO: Add the aesdsocket application and any other files you need to install
# See https://git.yoctoproject.org/poky/plain/meta/conf/bitbake.conf?h=kirkstone

inherit update-rc.d module

#FILES:${PN} += "${bindir}/aesdsocket"

#INITSCRIPT_PACKAGES = "${PN}" 
#INITSCRIPT_NAME:${PN} = "aesdsocket-start-stop.sh"

############ I2C LCD DRIVER Package #############

FILES:${PN} += "/lib/modules/${KERNEL_VERSION}/extra/i2c-lcd-driver.ko"

FILES:${PN} += "/etc/init.d/i2c-lcd-driver-load-unload.sh"

FILES:${PN} += "/etc/init.d/S99cputempmonitor"

INITSCRIPT_PACKAGES = "${PN}" 
INITSCRIPT_NAME:${PN} = "S99cputempmonitor"

############ THERMAL BRIDGE Package #############


FILES:${PN} += "/etc/init.d/thermal_lcd_bridge_daemon.sh"

############ PWM FAN DRIVER Package #############

FILES:${PN} += "/lib/modules/${KERNEL_VERSION}/extra/pwm_fan_driver.ko"

####################################################

# TODO: customize these as necessary for any libraries you need for your application
# (and remove comment)
TARGET_LDFLAGS += "-pthread -lrt"

# Use the kernel source and cross-compiler
KERNEL_SRC = "${STAGING_KERNEL_DIR}"
KERNEL_MODULE_AUTOLOAD += "i2c-lcd-driver"

do_configure () {
	:
}

do_compile () {
	unset LDFLAGS
	oe_runmake KERNEL_SRC=${STAGING_KERNEL_DIR} \
			   ARCH=arm \
			   CROSS_COMPILE=${TARGET_PREFIX} \
			   CFLAGS_MODULE="-fno-pic"
}

do_install () {
	# TODO: Install your binaries/scripts here.
	# Be sure to install the target directory with install -d first
	#install -d ${D}${bindir}
	install -d ${D}/lib/modules/${KERNEL_VERSION}/extra
	install -d ${D}/etc/init.d
	install -m 0755 ${S}/i2c-lcd-driver-load-unload.sh ${D}/etc/init.d
	install -m 0755 ${S}/i2c-lcd-driver.ko ${D}/lib/modules/${KERNEL_VERSION}/extra
	
	install -m 0755 ${S}/pwm_fan_driver.ko ${D}/lib/modules/${KERNEL_VERSION}/extra
	
	install -m 0755 ${S}/thermal_lcd_bridge_daemon.sh ${D}/etc/init.d
	#install -m 0755 ${S}/thermal_bridge_start_stop.sh ${D}/etc/init.d
	
	# Yocto variables ${D} and ${S} are useful here, which you can read about at 
	# https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-D
	# and
	# https://docs.yoctoproject.org/ref-manual/variables.html?highlight=workdir#term-S
	# See example at https://github.com/cu-ecen-aeld/ecen5013-yocto/blob/ecen5013-hello-world/meta-ecen5013/recipes-ecen5013/ecen5013-hello-world/ecen5013-hello-world_git.bb
	
	#install -m 0755 ${S}/aesdsocket ${D}${bindir}/
	install -m 0755 ${S}/i2c-lcd-driver.ko ${D}/lib/modules
	install -m 0755 ${S}/pwm_fan_driver.ko ${D}/lib/modules

	install -m 0755 ${S}/i2c-lcd-driver-load-unload.sh ${D}/etc/init.d/S99cputempmonitor

}
