import java.util.Scanner;

public class ML_TextCombatGame
{
    // Declaring the scanner to take in user input for the entire program
    private static Scanner scan = new Scanner (System.in);
    
    // Game variables that I wouldn't want to reset each time the game method runs
    public static final int bossFloor = 3;
    public static int floorNum = 0, playerHP = 100, playerStrength = 3, itemsRemaining = 5, critChance = 8, currency = 0;
    public static double playerDef = 1.0/2.0, shieldDurability = 0.9;
    
    public static void main (String [] args)
    {
        // Looping the game as long as the user want to play it
        do
        {
            game();
        }while(playAgain());
        
        // Saying goodbye to the user
        sopl("After a long day of fighting things, you head back home from the dungeon.");
        sopl("Exhausted, you head straight to bed and get a good night's sleep.");
        sopl("Thanks for playing !");
    }
    
    private static void game()
    {
        // Increases the count to the current floor of the dungeon 
        floorNum++;
        
        // Declaring variables
        int playerAttack, calculateCrit;
        int enemyHP = 100 + 50 * (floorNum - 1), enemyStrength = floorNum, critNum = 0, poisonDmg = playerHP / 16;
        boolean ultimateReady = false, shieldUp = false, lifesteal = false, poisoned = false, paralyzed = false, anger = false;
        double enemyDef = 2.0/5.0;
        int poisonCounter = 0, stunCounter = 0, lifestealCounter = 0, angerCounter = 0, maxAnger = 0, ultCounter = 0, atkCounter = 0;
        String[] itemsAvailable = {"Potion", "Carrot", "Bandages"};
        
        if(floorNum == 1 && critChance == 8) // Checking that this is the very first floor and not a reset floor
        {
            // Welcoming player to the game
            sopl("\fWelcome to the dungeon where you fight things for no reason !");
            sopl("Good luck in there soldier o7");
        }
        else if(floorNum == bossFloor) // Checking if this is the boss stage
        {
            sopl("\fHmmmm this could be the last time I see you soooooooo.");
            sopl("Welcome to the official final floor of the dungeon, you made it !");
            sopl("Awaiting you behind this door is a creature that's made up of all the souls of the ones who have tried to fight it.");
            sopl("Seeing that you've made it this far already, with no doubt, I'm sure that you'll be able to beat it !");
            sopl("Wishing the best of luck to you in there. :)");
        }
        else // Welcoming the user
        {
            sopl("\fWelcome back to the dungeon of what seems to be no end !");
            sopl("You are currently at floor " + floorNum + "!");
            sopl("Break a leg ! (try not to actually do that)");
        }
        
        lineBreak();
        
        // Set up shop to restore items and equipment if the user has money
        if(currency != 0)
        {
            char shopChoice, buyOrNot;
            int shieldPrice = 30, healPrice = 15;
            sopl("Hey there buddy !");
            sopl("Definitely normal shopkeeper here to sell you items and goods you may need on this adventure !");
            sopl("Take a look at what we have in stock and see what you want !");
            
            sopl("In stock : (S)hield repair for 30 coins, (H)ealing meds for 15 coins");
            
            sopl("Would you like anything? (y/n) You have " + currency + " coins remaining.");
            buyOrNot = scan.nextLine().toLowerCase().charAt(0);
            
            while(buyOrNot == 'y' && currency > healPrice) // Checking if the user has enough money to purchase the cheapest item
            {
                do
                {
                    sopl("Enter your choice: ");
                    shopChoice = scan.nextLine().toUpperCase().charAt(0);
                    
                    if(shopChoice == 'S')
                    {
                        if(currency < shieldPrice) // A check to remove possibilities of a negative balance
                        {
                            sopl("You don't have enough money !");
                            continue;
                        }
                        else
                        {
                            shieldDurability += 0.9;
                            currency -= shieldPrice;
                            sopl("You purchased a shield ! Shield durability increased to " + shieldDurability * 10 + " and you have " + currency + " coins left.");
                        }
                    }
                    else if (shopChoice == 'H')
                    {
                        itemsRemaining ++;
                        currency -= healPrice;
                        sopl("You purchased an item ! You have " + itemsRemaining + " items remaining and you have " + currency + " coins left.");
                    }
                    sopl("Would you like anything else? (y/n) You have " + currency + " coins remaining.");
                    buyOrNot = scan.nextLine().toLowerCase().charAt(0);
                }while(!validShopInput(shopChoice));
            }
            if(currency < healPrice && buyOrNot == 'y') // Shuts the shop down if the user tries to buy with not enough money
            {
                sopl("You dont have enough money, come back later !");
            }
            sopl("Goodbye ! o/");
            lineBreak();
        }
        
        if(floorNum == bossFloor) // Mini dialogue right before the boss fight
        {
            sopl("You step down, a little afraid after what was described of the boss.");
            sopl("But as the torches light up, you see this chonky blob of what seems like an enlarged version of the creatures you just fought.");
            sopl("You feel the confidence surging in you.");
        }
        
        // Main game loop 
        while(playerHP > 0 && enemyHP > 0)
        {
            String userChoice;
            char inputtedChar;
            
            sopl("Player HP: " + playerHP + "   <><><><>   Enemy HP:" + enemyHP);
            String actions = "Actions: (F)ight";
            if(shieldDurability != 0.0) // Checking if the shield is still intact
            {
                actions += ", (S)hield";
            }
            
            if(itemsRemaining != 0) // Checking for remaining items
            {
                actions += ", (I)tem";
            }
            
            if(ultCounter >= 5) // Checking if the ultimate is charged up
            {
                actions += ", (U)ltimate";
            }
            sopl(actions);
            
            do
            {
                sopl("Enter your choice: ");
                userChoice = scan.nextLine();
                inputtedChar = userChoice.toUpperCase().charAt(0);
                
                // Player's turn
                if(inputtedChar == 'F') // (F)ight
                {
                    // Player will deal damage based on their current hitpoints and damage is reduced by enemy's defense stat
                    playerAttack = (int) ((int)((Math.random() * (playerHP / 6))) * playerStrength * enemyDef);
                    calculateCrit = (int) (Math.random() * critChance);
                    
                    if(calculateCrit == critNum) // Unless an item is used, 1/8 chance to land a crit dealing 1.5x original damage
                    {
                        playerAttack *= 1.5;
                    }
                    
                    enemyHP = Math.max(0, enemyHP - playerAttack);
                    
                    if(lifesteal) //  Takes half the damage dealt to the enemy and converts it to own HP
                    {
                        playerHP = Math.min(100, playerHP += playerAttack / 2);
                    }
                    
                    
                    if(playerAttack == 0) // Special occurence when the player misses their attack
                    {
                        sopl("You charge at the opponent, realizing too late to jump over this root in front of you.");
                        sopl("You trip and miss, as the enemy stares at you at the ground, hitting them for a whopping " + playerAttack + "!");
                    }
                    else
                    {
                        String critHit = "A critical hit! ";
                        String attack = "You hit the opponent for " + playerAttack + ", the enemy is now left with " + enemyHP + " HP.";
                        if(critChance == critNum) // Checks for a crit
                        {
                            critHit += attack;
                            sopl(critHit);
                        }
                        else // Regular attack statement
                        {
                            sopl(attack);
                        }
                        
                        if(lifesteal) // Will drain the enemys health by half the attack dealt
                        {
                            sopl("You drain the enemy's health, restoring " + playerAttack / 2 + " HP.");
                            lifestealCounter++;
                            if(lifestealCounter == 3)
                            {
                                lifesteal = false;
                                lifestealCounter = 0;
                                sopl("The swarm of friendly bats wave goodbye to you and fly off into the sky. :D");
                            }
                        }
                    }
                    ultCounter++; // +1 ult count each time the user attacks
                    atkCounter++; // +1 atk count for the enemy to use a special move
                }
                else if (inputtedChar == 'S' && shieldDurability != 0.0)
                {
                    atkCounter = 0; // Resets the atk counter
                    shieldUp = true; // Reduces damage from enemy 
                    sopl("You put up your shield, protecting yourself from incoming damage.");                    
                }
                else if (inputtedChar == 'I' && itemsRemaining != 0)
                {
                    atkCounter = 0; // Resets the atk counter
                    if(playerHP == 100) // Checks if the player's at max HP so they won't waste any items.
                    {
                        sopl("You're already at max HP !");
                        continue;
                    }
                    else
                    {
                        int randoItem = (int) (Math.random() * itemsAvailable.length); 
                        
                        // Randomly selects items that each have their special effects.
                        if(itemsAvailable[randoItem] == "Potion")
                        {
                            playerHP = Math.min(100, playerHP += 30);
                            sopl("You feel refreshed as you consume a potion and restore 30 HP.");
                        }
                        else if(itemsAvailable[randoItem] == "Carrot")
                        {
                            playerHP = Math.min(100, playerHP += 10);
                            critChance /= 1.5;
                            sopl("You munch on a carrot and wow ! Your eyesight just got better ! \nCrit chance increased and HP restored by 10.");
                        }
                        else if(itemsAvailable[randoItem] == "Bandages")
                        {
                            playerHP = Math.min(100, playerHP += 15);
                            poisoned = false;
                            poisonCounter = 0;
                            sopl("You take out some bandages and wrap them around your wound, curing all negative effects and healing you for 15 HP.");
                        }
                        itemsRemaining--;
                    }
                    sopl("You have " + itemsRemaining + " item(s) remaining.");
                    break;
                }
                else if(inputtedChar == 'U' && ultCounter >= 5)
                {
                    char ultChoice;
                    atkCounter = 0;
                    sopl("You activate your ultimate ability, revealing three options to choose from: ");
                    sopl("(A)ttack+, (S)hield+, (I)tem+");
                                        
                    do
                    {
                        sopl("Enter your choice: ");
                        ultChoice = scan.nextLine().toUpperCase().charAt(0);
                        
                        if(ultChoice == 'A') // Big attack, increases player strength permanently; Acts the same as a normal attack
                        {
                            playerStrength *= 1.5;                                
                            
                            playerAttack = (int) (playerHP / 1.25 * enemyDef);
                            calculateCrit = (int) (Math.random() * critChance);
                            
                            if(calculateCrit == critNum) 
                            {
                                playerAttack *= 1.5;
                            }
                            
                            enemyHP = Math.max(0, enemyHP - playerAttack);
                            
                            String ultLine = "";
                            String critHit = "A critical hit! ";
                            String attack = "You hit the opponent for " + playerAttack + ", the enemy is now left with " + enemyHP + " HP.";
                            if(critChance == critNum) // Checks for a crit
                            {
                                critHit += attack;
                                sopl(ultLine + critHit);
                            }
                            else // Regular attack statement
                            {
                                sopl(ultLine + attack);
                            }
                            sopl("You feel a wave of energy after landing that attack, giving you a good boost to your strength.");
                        }
                        else if(ultChoice == 'S') // Permanent increase to player defense
                        {
                            playerDef += 1.0/9.0;
                            sopl("You find a random piece of armour lying on the ground and put it on, increasing your defense !");                   
                        }
                        else if(ultChoice == 'I') // Randomized spells with different effects
                        {
                            int randoSpell = (int) (Math.random() * 3);
                            sopl("You fish out an old spellbook that you found on the way here.");
                            sopl("You chant a random line out, praying that it would do something for you.");
                            if(randoSpell == 2) // Full heal
                            {
                                playerHP = 100;
                                sopl("The ground under you glows a bright green and you feel nice and cozy, as it was a spell that fully regenerated your health.");
                            }
                            else if (randoSpell == 1) // Lightning stun and dmg
                            {
                                paralyzed = true;
                                enemyHP = Math.max(0, enemyHP -= 20);
                                sopl("A large thundercloud slowly forms above the enemy's head, and after a good second, lightning strikes \ndown on them, dealing a good 20 damage to them.");
                                sopl("You go over to poke at your opponent's twitching body and it doesn't seem like they'll move for a while.");
                            }
                            else // Bats are summoned, can attack the user or help with lifesteal effect
                            {
                                int fiftyFifty = (int) (Math.random() * 2);
                                sopl("As you finish speaking, a swarm of bats surround you.");
                                if(fiftyFifty == 0)
                                {
                                    lifesteal = true;
                                    sopl("These bats seem to follow your command and can help sap the enemy's HP to \nmake half of it your own for the next 3 turns.");
                                }
                                else
                                {
                                    playerHP = Math.max(0, playerHP -= 15);
                                    sopl("The bats look like they're not very pleased to be woken up at this time of day.");
                                    sopl("They attack you for 15 and fly off afterwards.");
                                }
                            }
                        }
                    }while(!validInputUlt(ultChoice));
                    ultCounter = 0; // Reset the ult count
                    break;
                }
            }while(!validInput(inputtedChar, itemsRemaining, shieldDurability, ultCounter));
            
            sopl("");
            
            // Enemy's turn
            if(enemyHP > 0)
            {
                if(!paralyzed) // Checks if the player stunned them
                {
                    int enemyChoice = (int) (Math.random() * 10);
                    if(enemyChoice == 3 || atkCounter == 3) // Randomly selected/Checks if the player attacks consecutively for 3 turns and uses a special move
                    {
                        int ultOrHeal = (int) (Math.random() * 3);
                        if(atkCounter == 3)
                        {
                            atkCounter = 0; // Resets the atk counter if it triggered this
                        }
                        
                        if(ultOrHeal == 0) // A heal
                        {
                            enemyHP = Math.min(100, enemyHP += 10);
                            sopl("The opponent seems to have picked up a bug off the ground.");
                            sopl("They eat it (yuck!) and restore 10 HP.");
                        }
                        else if(ultOrHeal == 1)  // Anger : effect that doubles all attacks
                        {
                            anger = true;
                            maxAnger += 3;
                            sopl("The enemy seems to be mad at you.");
                            sopl("Its anger doubles its power for 3 turns !");
                        }
                        else // Poison : drains the player HP over a few turns
                        {
                            poisoned = true;
                            sopl("The enemy spits some purple goo at you and you fail to dodge it.");
                            sopl("The goo burns as it lands on your skin, decaying your health for 3 turns (an item could help!).");
                        }
                    }
                    else // Regular randomized attack, cannot be 0
                    {
                        int enemyAtk = (int) Math.max((int) (Math.random() * (enemyHP / 10)) * enemyStrength * playerDef, enemyStrength * 3.0 * playerDef);
                        if(anger)
                        {
                            enemyAtk *= 2;
                            maxAnger--;
                            
                            if(angerCounter == maxAnger)
                            {
                                anger = false;
                                maxAnger = 0;
                            }
                        }
                        
                        if(shieldUp && shieldDurability > 0.0) // Reduces attack if shield is up
                        {
                            enemyAtk *= shieldDurability / 2; // Enemy attack will be reduced by half of the shield's durability
                            shieldDurability = Math.round((shieldDurability - 0.1) * 10.0) / 10.0;
                            sopl("You bring up your shield, and brace yourself from impact.");
                        }
                        
                        playerHP = Math.max(0, playerHP -= enemyAtk);
                        if(anger) // Doubles attack if anger is triggered
                        {
                            sopl("The enemy leaps at you, scratching you for " + enemyAtk/2 + ", but the anger doubles it to " + enemyAtk + ".");
                            sopl("You are now left with " + playerHP + " HP. Anger resides in " + maxAnger + " turns."); 
                        }
                        else
                        {
                            sopl("The enemy leaps at you, scratching you for " + enemyAtk + ". You are now left with " + playerHP + " HP."); 
                        }                        
                        
                        if(shieldDurability <= 0.0 && shieldUp) // Checks the shield durability and informs user of it
                        {
                            sopl("Only being able to block a certain amount of hits, your beloved shield shatters in your hands.");
                        }
                        else if(shieldUp)
                        {
                            sopl("Your shield can take " + (Math.round(shieldDurability * 10) + 1) + " more hits.");
                        }
                    }
                }
                else // If they stun the opponent
                {
                    String stunned = "The enemy lies there, showing no signs of life";
                    String awake = ", but suddenly jolts awake. \nAs they slowly drag themselves up, it gives you enough time to make another move.";
                    if(stunCounter == 0)
                    {
                        sopl(stunned + ".");
                        stunCounter++;
                    }
                    else
                    {
                        paralyzed = false;
                        sopl(stunned + awake);
                    }
                }
                shieldUp = false;
            }
            
            if(poisoned) // Checks the user for poison
            {
                sopl(" ");
                if(poisonCounter == floorNum * 2) // Checks for the turns of poison
                {
                    poisoned = false;
                    poisonCounter = 0;
                    sopl("The poison seems to have subsided and you feel a lot better.");
                }
                else // Inflicts dmg
                {
                    poisonCounter++;
                    playerHP = Math.max(0, playerHP -= poisonDmg);
                    sopl("The wound burns, dealing " + poisonDmg + " and leaving you at " + playerHP + " HP.");
                }
            }
            // implement a difficulty feature that asks if they wanna keep going
            // increases the amount of health, strength of the enemy
            lineBreak();
        }
        
        if(playerHP == 0) // Lose
        {
            sopl("You let your eyes fall shut as the fatigue from fighting all those creatures have tired you out.");
            sopl("Game over!");
            floorNum = 1;
        }
        else // Floor win
        {
            sopl("You got rid of all the creatures on this floor! Woohooooooooooooooooo!");
            currency += playerHP / 2 * floorNum;
        }
    }
    
    // Asks user if they want to play again
    private static boolean playAgain()
    {
        if(playerHP == 0) // If the user loses
        {
            sopl("You slowly bring yourself up, feeling refreshed as you chug a potion down, bringing you back to 50 HP.");
            sopl("You ponder for a while, debating whether or not you should continue going or not.");
        }
        else if(floorNum == bossFloor) // If the user wins and the next stage is the boss
        {
            sopl("As usual, you approach the stairwell once you defeat the creature, but for some reason, this one has a mysterious feel to it.");
            sopl("It seems to give off a boss vibe to it, so as always:");
        }
        else // Regular ramp up to the rext floor
        {
            sopl("From the corner of your eye, you spot a staircase leading deeper into the dungeon.");
            sopl("The aura of the next floor seems stronger than the one you encountered just now.");
        }
        
        if(floorNum == bossFloor && playerHP > 0) // Full win
        {
            sopl("You did it !");
            sopl("You defeated the boss of the dungeon !");
            return false;
        }
        else // Next floor
        {
            sopl("Would you like to proceed? (y/n)"); // Asks the user if they wanna go on
            char again = scan.nextLine().toLowerCase().charAt(0);
            if(playerHP == 0 && again != 'y') // If the user faints and wants to reset progress with max HP, but keeps the povious buffs
            {
                sopl("Would you like to go back to the 1st floor? (y/n)");
                char reset = scan.nextLine().toLowerCase().charAt(0);
                floorNum = 0;
                playerHP = 100;
                return reset == 'y';
            }
            else if(playerHP == 0 && again == 'y') // If the user faints and wants to try the floor again, starting at half health
            {
                playerHP = 50;
                itemsRemaining += 3;
                shieldDurability += 0.4;
                
                sopl("After regaining some strength, you gather some courage and risk it by heading into the floor again.");
                return again == 'y';
            }
            else 
            {
                return again == 'y';
            }
        }
    }
    
    // Method to check if the user inputted a correct value
    private static boolean validInput(char inputChoice, int remItems, double sDura, int ultNum)
    {
        if(inputChoice == 'F')
        {
            return true;
        }
        else if(inputChoice == 'S' && sDura != 0.0)
        {
            return true;
        }
        else if(inputChoice == 'I' && remItems != 0)
        {
            return true;
        }
        else if(inputChoice == 'U' && ultNum >= 5)
        {
            return true;
        }return false;
    }
    
    // Method to check if the user inputted a correct value
    private static boolean validInputUlt(char inputChoice)
    {
        if(inputChoice == 'A')
        {
            return true;
        }
        else if(inputChoice == 'S')
        {
            return true;
        }
        else if(inputChoice == 'I')
        {
            return true;
        }return false;
    }
    
    // Method to check if the user inputs a valid value when in the shop
    private static boolean validShopInput(char inputChoice)
    {
        if(inputChoice == 'S')
        {
            return true;
        }
        else if(inputChoice == 'H')
        {
            return true;
        }return false;
    }
    
    // Method to print a line break made of '~'s
    private static void lineBreak()
    {
        sopl(" ");
        sopl("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        sopl(" ");
    }
    
    // Method to print the text without skipping to another line after printing
    private static void sop(String txt)
    {
        System.out.print(txt);
    }
    
    // Method to print inputted text on a line
    private static void sopl(String txt)
    {
        System.out.println(txt);
    }
}
