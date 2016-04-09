for(i = 0; i<1000; i++)
{
	//Randomize a number between 0 and 100
	var a = Math.floor((Math.random()*100)+1);

	//Randomize a number between 0 and 10
	var b = Math.floor((Math.random()*10)+1);

	//User
	var user = Math.floor((Math.random()*8)+1);
	console.log(user + "," + a + "," + b);
}
