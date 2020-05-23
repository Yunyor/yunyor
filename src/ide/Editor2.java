package ide;
import javax.swing.JFrame;


public class Editor2 {
    
    public  void main(String[] args) {
       
        Jf_ide obj = new Jf_ide();
     
        
        obj.setBounds(0, 0, 1980, 1020);
        obj.setTitle("Editor de Codigo");
        obj.setResizable(false
        
        );
        obj.setVisible(true);
        obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
}
