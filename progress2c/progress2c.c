#define _DEFAULT_SOURCE // workaround for usleep not being declared...

#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <stdbool.h>
#include <pthread.h>
#include <unistd.h> // usleep

#include <X11/Xlib.h>
#include <X11/Xutil.h>
#include <X11/Xos.h>
#include <X11/Xatom.h>

#include <signal.h>
#include <sys/ioctl.h> // Linux specific

#include "progress2c.h"

static _Bool active;

int main(int argc, char* argv[])
{
	if(argc == 2 && strcmp(argv[1], "--cmd") == 0) {
		progress_cmd();
		return EXIT_SUCCESS;
	} else {
		return progress_x();
	}
}

static void progress_cmd()
{
	active = true;
	if(signal(SIGINT, terminate_correctly) == SIG_ERR) {
		write_immediately("Unable to register signal handler.\n");
	}

	// Linux specific
	// See http://stackoverflow.com/questions/1022957/
	// 	getting-terminal-width-in-c
	struct winsize size;
	ioctl(STDOUT_FILENO, TIOCGWINSZ, &size);
	const size_t terminal_width = size.ws_col;

	write_immediately(PRE);
	size_t i = 0;
	while(active) {
		if(i == terminal_width) {
			write_immediately(CLEAR);
			i = 0;
		}
		write_immediately(BLOCK);
		i++;
		usleep(SLEEP_NS);
	}
	write_immediately(POST);
}

static void write_immediately(char* str)
{
	fputs(str, stdout);
	fflush(stdout);
}

static void terminate_correctly()
{
	active = false;
}

static int progress_x()
{
	struct draw_data d;
	// See http://www.x.org/releases/X11R7.7/doc/libX11/libX11/
	// 	libX11.html#Using_Xlib_with_Threads
	if(!XInitThreads()) {
		fputs("Failed to initialize threading support.\n", stdout);
		return EXIT_FAILURE;
	}
	// See http://www.paulgriffiths.net/program/c/srcs/helloxsrc.html
	d.display = XOpenDisplay(NULL);
	if(d.display == NULL) {
		fputs("Could not open X Display.\n", stderr);
		return EXIT_FAILURE;
	}
	const int nr = DefaultScreen(d.display);
	d.window = XCreateSimpleWindow(
		d.display, RootWindow(d.display, nr),
		DEFAULT_X, DEFAULT_Y, DEFAULT_W, DEFAULT_H, 1, BAR_FG, BAR_BG
	);
	XStoreName(d.display, d.window, "Progress C X");
	XSelectInput(d.display, d.window, ExposureMask | StructureNotifyMask);
	d.g = XCreateGC(d.display, d.window, 0, NULL);
	XSetForeground(d.display, d.g, BAR_FG);
	XMapWindow(d.display, d.window);

	// See http://cboard.cprogramming.com/linux-programming/
	// 	60466-xwindows-close-window-event.html
	Atom wmDelete = XInternAtom(d.display, "WM_DELETE_WINDOW", true);
	XSetWMProtocols(d.display, d.window, &wmDelete, 1);

	d.width  = DEFAULT_W;
	d.height = DEFAULT_H;

	XEvent event;
	pthread_t thread_id = 0; // workaround
	_Bool created = false;
	active  = true;
	while(active) {
		XNextEvent(d.display, &event);
		switch(event.type) {
		case Expose:
			if(!created) {
				created = true;
				thread_id = thread_init(&d);
			}
			break;
		case ConfigureNotify:
			d.width  = event.xconfigure.width;
			d.height = event.xconfigure.height;
			break;
		case ClientMessage:
			active = false;
			break;
		case 19: // I did not bother to find out what these are about
		case 21: // but they alway occurred and gave "Unhandled Event"
			break;
		default:
			fprintf(stderr, "Unhandled Event: %d\n", event.type);
		}
	}

	if(created) {
		d.terminate_thread = true;
		pthread_join(thread_id, NULL);
	}

	XFreeGC(d.display, d.g);
	XCloseDisplay(d.display);
	return EXIT_SUCCESS;
}

static pthread_t thread_init(struct draw_data* d)
{
	d->terminate_thread = false;
	pthread_t id;
	pthread_create(&id, NULL, &display_progress, d);
	return id;
}

static void* display_progress(void* raw)
{
	struct draw_data* d = (struct draw_data*)raw;
	const unsigned int pad_max = 2 * BAR_PADDING;
	unsigned int status = 0;
	unsigned int y, max_x;
	while(!d->terminate_thread) {
		XLockDisplay(d->display);
		XClearWindow(d->display, d->window);
		y = (d->height - BAR_HEIGHT) / 2;
		max_x = d->width - pad_max;
		XFillRectangle(
			d->display, d->window, d->g,
			BAR_PADDING, y, status++, BAR_HEIGHT
		);
		if(status > max_x) {
			status = 0;
		}
		// See http://www.dis.uniroma1.it/~liberato/screensaver/ball.c
		XFlush(d->display);
		XUnlockDisplay(d->display);

		usleep(BAR_REDRAW_NS);
	}
	return NULL;
}
