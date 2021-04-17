#!/bin/sh
#
# Progress Shell Script implementation, Copyright (c) 2012 Ma_Sys.ma.
# For furhter info send an e-mail to Ma_Sys.ma@web.de.
#

if [ "$PS1" = "" ] || [ "$1" = "--new" ]
then
	exec xterm -T Progress -bc -bg black +cm -cr green +cu -fg white \
		-geometry 32x1 +mesg +nul -rightbar +sb -sl 3 -u8 -en \
		UTF-8 +mb -fn terminus-16 -e $0
fi

WIDTH=32
TIME=0.5
BLOCK=█
PRE='\033[?25l\033[35;40;1m'
POST='\033[?25h\033[0;m\n'

i=0
back="\r"
while [ $i -lt $WIDTH ]
do
	back=$back" "
	i=$(($i + 1))
done
back=$back"\r"

trap ma_terminate 2

ma_terminate() {
	i=-1
}

printf "$PRE"
i=0
while ! [ $i = -1 ]
do
	printf "$BLOCK"
	if [ $i = $WIDTH ]
	then
		i=0
		printf "$back"
	fi
	i=$(($i + 1))
	sleep $TIME
done
printf "$POST"
