/*
 * main.c
 *
 *  Created on: 31-Aug-2015
 *      Author: kiran
 */

/*
 * Written in AVR Studio 5
 * Compiler: AVR GNU C Compiler (GCC)
 *
 * Created: 12/22/2011 4:39:50 PM
 * Author: AVR Tutorials
 * Website: www.AVR-Tutorials.com
 */

#define F_CPU	4000000UL
#include <avr/delay.h>
#include <avr/io.h>

/*LCD function declarations */
void LCD_send_command(unsigned char cmnd);
void LCD_send_data(unsigned char data);
void LCD_init();
void LCD_goto(unsigned char y, unsigned char x);
void LCD_print(char *string);
void LCD_blink();
void LCD_scroll(unsigned char direction);

#define LCD_DATA_PORT	PORTB
#define LCD_DATA_DDR	DDRB
#define LCD_DATA_PIN	PINB

#define LCD_CNTRL_PORT	PORTC
#define LCD_CNTRL_DDR	DDRC
#define LCD_CNTRL_PIN	PINC

#define LCD_RS_PIN		0
#define LCD_RW_PIN		1
#define LCD_ENABLE_PIN	2

int main(void)
{
	unsigned char i;

    LCD_init();
	LCD_goto(1,2);
	LCD_print("AVR TUTORIALS");
	LCD_goto(2,3);
	LCD_print("WELCOMES YOU");

	while(1)
    {
        for(i=0;i<2;i++)
			LCD_blink();

		for(i=0;i<2;i++)
			LCD_scroll(0);

		for(i=0;i<4;i++)
			LCD_scroll(1);

		for(i=0;i<2;i++)
			LCD_scroll(0);
    }
}

/* This function sends a command 'cmnd' to the LCD module*/
void LCD_send_command(unsigned char cmnd)
{
	LCD_DATA_PORT = cmnd;
	LCD_CNTRL_PORT &= ~(1<<LCD_RW_PIN);
	LCD_CNTRL_PORT &= ~(1<<LCD_RS_PIN);

	LCD_CNTRL_PORT |= (1<<LCD_ENABLE_PIN);
	_delay_us(2);
	LCD_CNTRL_PORT &= ~(1<<LCD_ENABLE_PIN);
	_delay_us(100);
}

/* This function sends the data 'data' to the LCD module*/
void LCD_send_data(unsigned char data)
{
	LCD_DATA_PORT = data;
	LCD_CNTRL_PORT &= ~(1<<LCD_RW_PIN);
	LCD_CNTRL_PORT |= (1<<LCD_RS_PIN);

	LCD_CNTRL_PORT |= (1<<LCD_ENABLE_PIN);
	_delay_us(2);
	LCD_CNTRL_PORT &= ~(1<<LCD_ENABLE_PIN);
	_delay_us(100);
}

void LCD_init()
{
	LCD_CNTRL_DDR = 0xFF;
	LCD_CNTRL_PORT = 0x00;
	LCD_DATA_DDR = 0xFF;
	LCD_DATA_PORT = 0x00;

	_delay_ms(10);
	LCD_send_command(0x38);
	LCD_send_command(0x0C);
	LCD_send_command(0x01);
	_delay_ms(10);
	LCD_send_command(0x06);
}

/* This function moves the cursor the line y column x on the LCD module*/
void LCD_goto(unsigned char y, unsigned char x)
{
	unsigned char firstAddress[] = {0x80,0xC0,0x94,0xD4};

	LCD_send_command(firstAddress[y-1] + x-1);
	_delay_ms(10);
}

void LCD_print(char *string)
{
	unsigned char i;

	while(string[i]!=0)
	{
		LCD_send_data(string[i]);
		i++;
	}
}

void LCD_blink()
{
	LCD_send_command(0x08);
	_delay_ms(250);
	LCD_send_command(0x0C);
	_delay_ms(250);
}

void LCD_scroll(unsigned char direction)
{
	if(direction == 0)
		LCD_send_command(0x18);
	else
		LCD_send_command(0x1C);

	_delay_ms(500);
}
