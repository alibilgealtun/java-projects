import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static Random rand = new Random();
    public static int roundCounter=0, n,score1=0,score2=0;
    static Scanner scores, scan = new Scanner(System.in);
    public static boolean firstTournament = false;


    static Queue q1 = new Queue(12), q2 =new Queue(12), q3, q4;
    static Stack allCards = new Stack(13);
    static Stack S1, S2;


    public static void main(String[] args) {

        try {
            File text = new File("highscoretable.txt");
            scores = new Scanner(text);
        } catch (FileNotFoundException e) {
            System.out.println("Text file not found! Please check path again.");
            System.exit(0);
        }

        sortQueue();


        fillStack(); //Fills allStack with  "A,2,3...,J,Q,K" cards.

        String playAgain="Y";


        //Game playing part
        while (Objects.equals(playAgain, "Y")){
            do {
                try{
                    System.out.print("Please enter a number between 7 and 10: ");
                    n = scan.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println("Invalid number. ");
                }
                scan.nextLine();
            } while( n< 7 || n>10);

            System.out.println();

            q3 = new Queue(13);
            q4 = new Queue(13);

            fillQ3(); // Fills bag1 with all cards. (q3)

            S1 = new Stack(n);
            S2 = new Stack(n);



            //Make 2 cards all randomly.
            makeRandomCard(S1);
            makeRandomCard(S2);

            //Sort the cards.
            sortCard(S1);
            sortCard(S2);

            displayRound();

            //Play rounds until one of cards become empty.
            while(!S1.isEmpty() && !S2.isEmpty()) {
                round();
            }

            //Displaying game is over and who is winner.
            System.out.println("\nGame Over! \n");
            if (score1>score2){
                System.out.println("Winner is Player1 with "+ score1+" points.");
                System.out.print("What is your name: ");
                String name = scan.next();
                addNameToQueue(name, score1);
            }
            else if (score2>score1){
                System.out.println("Winner is Player2 with "+ score2+" points.");
                System.out.print("What is your name: ");
                String name = scan.next();
                addNameToQueue(name, score2);
            }
            else{
                System.out.println("Tie!");
            }

            displayTable();

            //Play again
            System.out.print("Do you want to play again? (Y/N): ");
            playAgain = scan.next().toUpperCase();
            if (Objects.equals(playAgain, "N")){
                playAgain="N";
                System.out.println("\nWe hope you enjoyed! Exiting... ");
            }
            else if (Objects.equals(playAgain,"Y")){
                playAgain="Y";
            }
            else{
                System.out.println("Wrong input. Exiting...");
            }
            roundCounter=0;
            firstTournament=false;
            score1=0;score2=0;

        }



    }
    public static void sortQueue(){
        /*
         ALGORITHM EXPLANATION
             In this method, I used 3 stacks.
             scoreStacks is for every line, I stored every line in this stack.
             Then I used maxScore integer to check if current line has the current maximum score.
             If it does, I pushed it into maxStack. Else, I pushed it into others stack.
             Then I pulled maxStack's last member (It has the maximum score) to queue.
             Then I pushed maxStack member and others stack to scoreStacks again and did it until scoreStacks is empty.
             This made queue sorted.
         */
        Stack scoreStacks = new Stack(12);
        Stack maxStack = new Stack(12);
        Stack others= new Stack(12);
        int maxScore;

        //Adding scores to others stack first.
        while (scores.hasNextLine()){
            others.push(scores.nextLine());
        }
        //Reason adding them from others to scoreStacks is to read them in the right order as txt file.
        while(!others.isEmpty()){
            scoreStacks.push(others.pop());
        }


        //while loop with checking if stack is empty
        while (!scoreStacks.isEmpty()){
            maxScore = 0;
            //Iterating over stack elements
            for (int i = 0; i < 12; i++){
                if (scoreStacks.peek()!=null) {
                    try{
                        String line = scoreStacks.pop().toString();
                        int score = Integer.parseInt(line.split(" ")[1]);
                        if (score > maxScore) {
                            //If current score is bigger than maxScore, I pushed it into maxStack.
                            maxStack.push(line);
                            maxScore = score;
                        }
                        else{
                            others.push(line);
                    }
                    }
                    catch(Exception e){
                        System.out.println("Text format is not appropriate! It must be: 'Name score'");
                    }
                }
            }
            //maxStack's last member is the biggest score in all queue. I enqueued it.
            try {
                String biggest = maxStack.pop().toString();
                q1.enqueue(biggest.split(" ")[0]);
                q2.enqueue(Integer.parseInt(biggest.split(" ")[1]));
            }
            catch(Exception ignored){}
            //Then I pushed others and maxStack members to scoreStacks to do all these again until every element is enqueued.
            while (!maxStack.isEmpty()){
                scoreStacks.push(maxStack.pop());
            }
            while (!others.isEmpty()){
                scoreStacks.push(others.pop());
            }

        }




    }

    public static void fillQ3(){
        q3.enqueue("A");q3.enqueue("2");q3.enqueue("3");q3.enqueue("4");q3.enqueue("5");q3.enqueue("6");q3.enqueue("7");q3.enqueue("8");q3.enqueue("9");q3.enqueue("10");q3.enqueue("J");q3.enqueue("Q");q3.enqueue("K");
    }
    public static void fillStack(){
        allCards.push("A");
        allCards.push("2");
        allCards.push("3");
        allCards.push("4");
        allCards.push("5");
        allCards.push("6");
        allCards.push("7");
        allCards.push("8");
        allCards.push("9");
        allCards.push("10");
        allCards.push("J");
        allCards.push("Q");
        allCards.push("K");
    }

    public static void round(){
        /*
            Every round, this function creates a temporary queue to empty q3 into it.
            It randomly chooses a number, then q3 is carried into temp until that number.
            Then it selects the number, assigns it into a variable called val and enqueues q4 with it.
            Then continues to empty q3.
            Then when q3 is empty, it fills q3 from temp again. The selected value is now in q4.
         */
        Queue temp = new Queue(13-roundCounter);
        System.out.println();

        int randomNum = rand.nextInt(1,14-roundCounter);
        //Empty q3 until the number we want.
        for (int i = 0; i < randomNum-1 ;i++)
            temp.enqueue(q3.dequeue());

        //Assign the number we want and add it into q4.
        Object val = q3.dequeue();
        System.out.println(roundCounter+1+". selected value: " + val);
        q4.enqueue(val);

        //Empty the remaining q3.
        for (int i = 0; i< (13 - randomNum - roundCounter);i++){
            temp.enqueue(q3.dequeue());
        }

        roundCounter++;

        //Fill q3 with temporary queue. Now the selected value is not in the q3.
        for (int i = 0; i < 13 - roundCounter; i++){
            q3.enqueue(temp.dequeue());
        }


        //Check if the selected value is in card 1
        if (isInCard(S1,val)) {
            score1+=10;
            if (S1.size() == (n-4) && !firstTournament){
                firstTournament=true;
                score1-=10; //I'm subtracting because I want it to look not as +10 and +20. Document wants +30 as one.
                displayRound();
                score1+=30;
                System.out.println("\nFirst tournament is completed.");

            }

        }
        else{
            score1-=5;
        }

        //Check if the selected value is in card 2
        if (isInCard(S2,val)){
            score2+=10;
            if (S2.size() == n-4 && !firstTournament){
                score2-=10;
                firstTournament=true;
                displayRound();
                System.out.println("\nFirst tournament is completed.");
                score2+=30;
            }
        }
        else{
            score2-=5;
        }

        //Check if the cards are empty
        if (S1.isEmpty() || S2.isEmpty()){
            if (S1.isEmpty() && S2.isEmpty()){
                score1+=25;score2+=25;
            }
            else if (S1.isEmpty()){
                score1+=50;
            }
            else
                score2+=50;
        }

        displayRound();

    }
    public static void displayTable(){
        /*
            In this function, I used temporary name queue and temporary score queue.
            I firstly emptied q1 and q2, displayed it one by one and added it into temporary queues.
            Then added all of them again into q1 and q2.
         */
        System.out.println("\nHigh      Score      Table");
        Queue tempNames = new Queue(q1.size());
        Queue tempScores = new Queue(q2.size());
        while(!q1.isEmpty()){
            String valueName =  (String) q1.dequeue();
            Object valueScore = q2.dequeue();

            System.out.print( valueName +" ");
            for (int i = 0; i < 9 - valueName.length() ; i++)
                System.out.print(" ");
            System.out.println(valueScore);

            tempNames.enqueue(valueName);
            tempScores.enqueue(valueScore);
        }
        while (!tempNames.isEmpty()){
            q1.enqueue(tempNames.dequeue());
            q2.enqueue(tempScores.dequeue());
        }
        System.out.println();
        writeFile();
    }
    public static void writeFile() throws RuntimeException {
        /*
            Writes q1 and q2 to the file. Uses same algorithm with displaying table.
            Takes values, writes them and stores them into temporary queues.
            Then takes all of them from temporary queues to q1 and q2 again.
         */
        try {
            FileWriter outputFile = new FileWriter("HighScoreTableUpdated.txt");
            Queue tempNames = new Queue(q1.size());
            Queue tempScores = new Queue(q2.size());
            while(!q1.isEmpty()){
                String valueName =  (String) q1.dequeue();
                Object valueScore = q2.dequeue();

                outputFile.write(valueName +" "+valueScore+"\n");
                tempNames.enqueue(valueName);
                tempScores.enqueue(valueScore);
            }
            while (!tempNames.isEmpty()){
                q1.enqueue(tempNames.dequeue());
                q2.enqueue(tempScores.dequeue());
            }
            outputFile.close();
        } catch (IOException e) {
            System.out.println("An error occurred about file.");
            e.printStackTrace();
        }
    }
    public static void displayRound(){

        System.out.print("Player1: ");
        readCard(S1);
        System.out.print("     Score: "+score1+"        Bag1: ");
        readQueue(q3);
        System.out.println();

        System.out.print("Player2: ");
        readCard(S2);
        System.out.print("     Score: "+score2+"        Bag2: ");
        readQueue(q4);
        System.out.println();


    }
    public static void readQueue(Queue q){
        //Reads queues with storing them into temporary queue and taking them back when it is all finished.
        int size = q.size();
        Queue temp = new Queue(size);
        for (int i =0; i<size;i++){
            Object value = q.dequeue();
            System.out.print(value+" ");
            temp.enqueue(value);
        }
        for (int i = 0; i<size;i++){
            q.enqueue(temp.dequeue());
        }
    }
    public static boolean isInCard(Stack card, Object value){
        /*
            This function checks if the object value is in card or not.
            It empties all cards in the stack one by one, checks every one of them
            if any one of cards matches the value. Then it fills the stack again with using temporary stack.
            I used flag to see if it is in stack or not.
         */
        Stack temp = new Stack(n);
        boolean flag = false;
        while (!card.isEmpty())
        {
            Object val = card.pop();
            //If the current value is the searched value, it doesn't push it to the temporary queue. It will be removed.
            if (val == value)
                flag = true;
            else
                temp.push(val);
        }

        while(!temp.isEmpty()){
            card.push(temp.pop());
        }
        return flag;
    }
    public static void readCard(Stack card){
        Stack temp = new Stack(13);
        int size = card.size();
        for (int i = 0; i < size;i++)
        {
            Object value = card.pop();
            System.out.print(value + " ");
            temp.push(value);
        }
        for (int k = 0; k< size; k++){
            card.push(temp.pop());
        }
    }
    public static void makeRandomCard(Stack card){
        int counter = 0;
        Stack temp = new Stack(13);

        //Iterate n times (the user input) because n value will be in cards.
        for (int k = 0; k<n; k++) {
            //random number is to take random value from a stack
            int randomNum = rand.nextInt(0,13-counter);


            for (int i = 0; i < (randomNum - 1); i++)
                temp.push(allCards.pop());

            card.push(allCards.pop());

            for (int i = 0; i < (randomNum - 1); i++)
                allCards.push(temp.pop());

        counter++;
        }

        for (int z = 0; z<(13-n);z++){
            allCards.pop();
        }

        fillStack();
    }
    public static void addNameToQueue(String name, int score){
        //Adds names to queue using 2 temporary queues.
        int size = q2.size();

        Queue tempQ1 = new Queue(12);
        Queue tempQ2 = new Queue(12);

        //Iterating over q1 player's score is bigger than the current member's score.
        for (int i =0 ; i < size; i++){
            int currentScore =(int) q2.dequeue();
            //If score is bigger than the current value, add before current value
            // then add the current value and break.
            if (score > currentScore){
                tempQ1.enqueue(name);
                tempQ2.enqueue(score);
                //This if block controls maximum capacity. if 'i' is already 12, it stops enqueue process.
                if (i!=12){
                    tempQ1.enqueue(q1.dequeue());
                    tempQ2.enqueue(currentScore);
                break;}
            }
            tempQ1.enqueue(q1.dequeue());
            tempQ2.enqueue(currentScore);
        }
        //Continue dequeue process until q1 is empty.
        while(!q1.isEmpty()){
            tempQ1.enqueue(q1.dequeue());
            tempQ2.enqueue(q2.dequeue());
        }
        //Then copy q1 with q2.
        q1 = tempQ1;
        q2 = tempQ2;

    }

    public static void sortCard(Stack card) {
        Stack temp = new Stack(card.size());
        Stack others = new Stack(card.size());
        Stack sortedTemp = new Stack(card.size());

        while (!card.isEmpty())
            temp.push(card.pop());


        while (!temp.isEmpty()){
            int max = 0;
            while (!temp.isEmpty()) {
                int num;
                String value = (String) temp.pop();
                num = switch (value) {
                    case "A" -> 1;
                    case "J" -> 11;
                    case "Q" -> 12;
                    case "K" -> 13;
                    default -> Integer.parseInt(value);
                };
                if (num > max) {
                    max = num;
                    sortedTemp.push(value);
                } else {
                    others.push(value);
                }
            }

            card.push(sortedTemp.pop());

            while (!sortedTemp.isEmpty())
                temp.push(sortedTemp.pop());
            while(!others.isEmpty())
                temp.push(others.pop());

        }
    }
}