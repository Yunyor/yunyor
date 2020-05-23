package ide;

import java.awt.FileDialog;


public class Editar {
    Jf_ide frame;
    
    public Editar(Jf_ide frame){
        this.frame = frame;
    }
    
    public void undo(){
        frame.editManager.undo();
    }
    
    public void redo(){
        frame.editManager.redo();
    }
    
}
