#include <avr/io.h>
#include <util/delay.h>
 
#include "lcd.h"
 
int main(void)
{
    lcd_init(LCD_DISP_ON_CURSOR); /* initialize lcd, display on, cursor on */
                                  /* for more options for
                                  /* lcd_init(), view lcd.h file
    while(1)                      /* run continuously */
    {
        lcd_clrscr();             /* clear screen of lcd */
        lcd_home();               /* bring cursor to 0,0 */
        lcd_puts("hello");        /* type something random */
        lcd_gotoxy(0,1);          /* go to 2nd row 1st col */
        lcd_puts("maxEmbedded");  /* type something random */
        _delay_ms(50);            /* wait 50ms */
    }
}
