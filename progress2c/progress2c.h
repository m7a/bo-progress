// Window
#define DEFAULT_X  0x40
#define DEFAULT_Y  0x40
#define DEFAULT_W 0x100
#define DEFAULT_H  0x28

// "Progress" Bar
#define BAR_BG        0x000000
#define BAR_FG        0xffff00
#define BAR_PADDING       0x10
#define BAR_HEIGHT        0x08
#define BAR_REDRAW_NS   0x2000

// Commandline Mode
#define BLOCK    "█" // These are actually three bytes
#define PRE      "\x1b[?25l\x1b[35;40;1m"
#define POST     "\x1b[?25h\x1b[0;m\n"
#define SLEEP_NS 100000
#define CLEAR    "\x1b[1K\x1b[0G" 

struct draw_data {
	Display* display;
	Window window;
	GC g;
	unsigned int width;
	unsigned int height;
	_Bool terminate_thread;
};

static void progress_cmd();
static void write_immediately(char* str);
static void terminate_correctly();

static int progress_x();
static pthread_t thread_init(struct draw_data* d);
static void* display_progress(void* raw);

