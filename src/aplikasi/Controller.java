package aplikasi;
import fti.ti.sda.BinaryTreeNodeSDA;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

import java.util.*;

public class Controller {
    //mendeklarasi container & control pada java
    @FXML
    private AnchorPane halamanDepan;
    @FXML
    private AnchorPane isiSaldo;
    @FXML
    private AnchorPane gamePane;


    @FXML
    private TextField saldoMember;
    @FXML
    private TextField jumlahTopUp;
    @FXML
    private TextField jumlahBet;
    @FXML
    private TextField userAngka1;
    @FXML
    private TextField userAngka2;
    @FXML
    private TextField userAngka3;
    @FXML
    private TextField userAngka4;
    @FXML
    private TextField hasilAngka1;
    @FXML
    private TextField hasilAngka2;
    @FXML
    private TextField hasilAngka3;
    @FXML
    private TextField hasilAngka4;


    @FXML
    private Button mode4;
    @FXML
    private Button mode3;
    @FXML
    private Button mode2;


    @FXML
    private Label jumlahMode;



    private int saldo = 0;
    private int modeGame = 0;
    private int totalTopUp = 0;
    private int totalWin = 0;
    private double reward = 0;
    private int spamGame = 0;
    private List<Integer> validList = new ArrayList<>();
    private List<TextField> listUserAngka = new ArrayList<>();

    // initialize ( method yang di eksekusi saat program dijalankan)
    @FXML
    public void initialize(){
        saldoMember.setText(Integer.toString(saldo));
        listUserAngka.add(userAngka1);
        listUserAngka.add(userAngka2);
        listUserAngka.add(userAngka3);
        listUserAngka.add(userAngka4);
        playMP3("win.mp3");

        home();
    }

    // Method untuk mengganti halaman
    private void hideAndShow(boolean one, boolean two,boolean three){
        halamanDepan.setVisible(one);
        isiSaldo.setVisible(two);
        gamePane.setVisible(three);

    }

    //method untuk menampilkan halaman topup
    public void topup(){
        hideAndShow(false,true,false);
    }

    //method untuk menampilkan halaman utama
    public void home(){
        saldoMember.setText(Integer.toString(saldo));
        hideAndShow(true,false,false);
    }

    //method untuk menambah saldo
    public void topUpSaldo(){

        //True jika form tidak di isi
        if(jumlahTopUp.getText().isEmpty()){
            panel("Mohon isi nominal top up");
        }else{
            try{
                //jumlah maksimal topup
                if(totalTopUp > 2){
                    panel("Anda sudah mencapai limit topup harian");
                }else{
                    //memeriksa jika saldo yang di topup tidak semestinya / minus / kebanyakan
                    if(Integer.parseInt(jumlahTopUp.getText()) > 1000000 || Integer.parseInt(jumlahTopUp.getText()) < 1){
                        panel("Nominal top up anda tidak wajar");
                    }else{
                        Thread.sleep(500);
                        playMP3("topup.mp3");
                        saldo += Integer.parseInt(jumlahTopUp.getText());
                        panel("Top Up Berhasil");
                        totalTopUp++;
                    }

                }

            //menampilkan error jika form yang di isi huruf
            }catch (Exception e){
                panel("Mohon isi dengan benar");

            }

        }
    }

    //method alert serba guna , tinggal ganti textnya
    private void panel(String text){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Caution");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }


    //method untuk menampilkan halaman game
    public void gamePane(ActionEvent e){

        //memeriksa mode game apa yang di tekan
        if(e.getSource().equals(mode4)){
            modeGame = 4;
        }else if(e.getSource().equals(mode3)){
            modeGame = 3;
        }else if(e.getSource().equals(mode2)){
            modeGame = 2;
        }

        //true jika jumlah bet tidak kosong / kurang dari jumlah saldo
        if(!jumlahBet.getText().isEmpty() && Integer.parseInt(jumlahBet.getText()) <= saldo && Integer.parseInt(jumlahBet.getText()) > 1){
            hideAndShow(false,false,true);
            jumlahMode.setText(Integer.toString(modeGame));

            //mengisi value reward sesuai mode yang di pilih
            if(modeGame == 4)
                reward = Integer.parseInt(jumlahBet.getText()) * 50;
            else  if(modeGame == 3)
                reward = Integer.parseInt(jumlahBet.getText()) * 10;
            else  if(modeGame == 2)
                reward = Integer.parseInt(jumlahBet.getText()) * 3;

        }else{
            panel("Masukan jumlah bet dengan benar / saldo kurang");
        }
    }

    //method untuk menjalankan gamenya
    public void mulaiRoll(){
        // variabel untuk memerika jumlah angka yang sama
        totalWin = 0;
        //true jika saldo melebihi jumlah betnya
        if(saldo >= Integer.parseInt(jumlahBet.getText())){
            //variabel untuk memeriksa jumlah input
            int jumlahInput = 0;

            //looping untuk memeriksa angka dari user
            for(TextField i : listUserAngka)
            jumlahInput += checkAngka(i,1); //akan mereturn 0 / 1

            boolean error = false;
            //looping untuk memeriksa angka yang di berikan user
            for(int check:validList){
                if(check >= 10 || check < 1){
                    //jika user memasukkan angka lebih dari 10 atau kurang dari 1 error akan true
                    error = true;
                    break;
                }
            }

            //reset valid list
            validList = new ArrayList<>();

            //true jika player belum mereset game
            if(spamGame > 0){
                panel("Mohon tekan tombol reset terlebih dahulu");
                //true jika player memasukkan angka tidak sesuai mode game , atau error tadi true
            }else if(jumlahInput != modeGame || error){
                panel("Isi minimal atau maksimal "+modeGame+" angka, dan angka 1-9");
            }else{
                spamGame++;
                try{
                    Thread.sleep(500);
                }catch (Exception e){
                    //biar ngelag aja :V
                }

                //mendeklarasikan class random
                Random roll = new Random();
                //membuat root pada binary search tree
                BinaryTreeNodeSDA<Integer> root = new BinaryTreeNodeSDA<>(roll.nextInt(9) +1,null);

                hasilAngka1.setText(Integer.toString(root.getData()));
                //memasukkan angka pada binary search tree
                add(root, roll.nextInt(9) +1,hasilAngka2);
                add(root, roll.nextInt(9)+1,hasilAngka3);
                add(root, roll.nextInt(9)+1,hasilAngka4);

                //looping untuk merubah kotak angka tebakan selain yang di isi menjadi 0
                for(TextField i : listUserAngka)
                checkAngka(i,2);

                //looping untuk memeriksa jika ada yang sama menggunakan inorder
                for(TextField i : listUserAngka)
                search(root, Integer.parseInt(i.getText()));

                //true jika menang
                if(totalWin == modeGame){
                    playMP3("win.mp3");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("WINNNNNNNER!!");
                    alert.setHeaderText(null);
                    alert.setContentText("JACKPOTT, anda mendapatkan "+reward);
                    saldo += reward;
                    alert.showAndWait();
                }else {
                    playMP3("fail.mp3");
                    panel("Anda kurang beruntung :(");
                    saldo -= Integer.parseInt(jumlahBet.getText());
                }

            }
        }else{
            panel("Maaf saldo anda habis");
        }

    }

    //method untuk mengecheck angka dari user
    private int checkAngka(TextField textField, int pilih){
        if(textField.getText().isEmpty()){
            if(pilih == 2) textField.setText("0");
            return 0;
        }else if(!textField.getText().isEmpty() && pilih ==1){
            validList.add(Integer.parseInt(textField.getText()));
            return 1;
        }else {
            return 1;
        }

    }

    private void add(BinaryTreeNodeSDA<Integer> root, int data, TextField textField){
        while(true){
            if(data < root.getData()){
                if(root.getLeftChild() == null){
                    root.setLeftChild(new BinaryTreeNodeSDA<>(data,root));
                    textField.setText(Integer.toString(data));
                    break;
                }else{
                    root = root.getLeftChild();
                }
            }else{
                if(root.getRightChild() == null){
                    root.setRightChild(new BinaryTreeNodeSDA<>(data,root));
                    textField.setText(Integer.toString(data));
                    break;
                }else{
                    root = root.getRightChild();
                }
            }
        }

    }
    private void search(BinaryTreeNodeSDA<Integer> root, int data){

            if(root == null) return;

            search(root.getLeftChild(),data);
            if(root.getData() == data && !root.isVisited()){
                totalWin++;
                root.visited();
                return;
            }


            search(root.getRightChild(),data);


    }

    //method untuk mereset game
    public void reset(){
        userAngka1.setText("");
        userAngka2.setText("");
        userAngka3.setText("");
        userAngka4.setText("");
        hasilAngka1.setText("");
        hasilAngka2.setText("");
        hasilAngka3.setText("");
        hasilAngka4.setText("");
        spamGame = 0;
    }
    
    private void playMP3(String judul){
        try{
            String musicFile = "src/aplikasi/sound/"+judul;
            Media sound = new Media(new File(musicFile).toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        }catch (Exception e){
            System.err.println(e.getMessage());
        }

    }
}
