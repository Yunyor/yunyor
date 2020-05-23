package ide;
import com.mxgraph.model.mxGraphModel;
import javax.swing.JFrame;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.swing.SwingUtilities;

/**
 *
 * @author marangon.pietro
 */
public class intento extends JFrame{
    
        private mxGraph graph       = new mxGraph();
        private Object parent       = graph.getDefaultParent();
        
        private List<Object> lista  = new ArrayList<>();
        private Map ass             = new HashMap();
        
        private int paddt           = 20;
        private int paddl           = 20;
        
	public intento(String title)
	{
		super(title);

		graph.getModel().beginUpdate();
                
		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		getContentPane().add(graphComponent);
                this.applyEdgeDefaults();
	}
        
        private void applyEdgeDefaults() {
            // Settings for edges
            Map<String, Object> edge = new HashMap<>();
            edge.put(mxConstants.STYLE_ROUNDED, true);
            edge.put(mxConstants.STYLE_ORTHOGONAL, false);
            edge.put(mxConstants.STYLE_EDGE, "elbowEdgeStyle");
            edge.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
            edge.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
            edge.put(mxConstants.STYLE_HORIZONTAL, mxConstants.ALIGN_MIDDLE);
            edge.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
            edge.put(mxConstants.STYLE_STROKECOLOR, "#000000"); // default is #6482B9
            edge.put(mxConstants.STYLE_FONTCOLOR, "#6482B9");

            mxStylesheet edgeStyle = new mxStylesheet();
            edgeStyle.setDefaultEdgeStyle(edge);
            this.graph.setStylesheet(edgeStyle);
            //graphComponent.setConnectable(false);
        }
        
        public void add(String nome, int id){
            String vId = UUID.randomUUID().toString();
            
            if(id > 0)
                this.paddl = 120*id;
            else
                this.paddl = 20; 
   
            this.lista.add(
                    graph.insertVertex(parent, vId, nome, paddl, paddt, 80,30)
            );
            
            this.paddt+=50;
                
            ass.put(id, vId);

            if(id > 0)
            this.graph.insertEdge(parent, null, null,
                                ((mxGraphModel) graph.getModel()).getCell((String) ass.get(id-1)),
                                ((mxGraphModel) graph.getModel()).getCell((String) ass.get(id))
            );

        }
        
        public void out(){
            this.graph.getModel().endUpdate();
        }
       

}
