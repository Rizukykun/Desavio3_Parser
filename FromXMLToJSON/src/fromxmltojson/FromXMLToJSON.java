/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fromxmltojson;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Administrador
 */
public class FromXMLToJSON
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception
    {
        
        FileDialog dialog = new FileDialog((Frame) null, "Select File to Open");
        dialog.setMode(FileDialog.LOAD);
        dialog.setVisible(true);
        String file = dialog.getDirectory() + dialog.getFile();
        if (dialog.getFile() == null){
            System.exit(0);
        }
        File entrada = new File(file);
        Scanner xml = new Scanner(entrada);
        StringBuilder sb = new StringBuilder();
        boolean appender = false, tagger = false, jumper = false;
        ArrayList<Objeto> lista = new ArrayList<Objeto>();
        Objeto o;
        while (xml.hasNextLine())
        {
            for (char c : xml.nextLine().toCharArray())
            {
                if (c == '<')
                {
                    appender = true;
                    tagger = true;
                    jumper = false;
                    if (sb.length() != 0)
                    {
                        lista.get(lista.size() - 1).setValor(sb.toString());
                        sb.setLength(0);
                    }
                } else if (c == '>' && appender)
                {
                    o = new Objeto();
                    o.setTag(sb.toString());
                    sb.setLength(0);
                    appender = false;
                    tagger = false;
                    lista.add(o);
                } else if (c == '/' && tagger)
                {
                    jumper = true;
                    appender = false;
                } else if (!jumper)
                {
                    sb.append(c);
                }
            }
        }
        sb.setLength(0);
        sb.append("{" + System.lineSeparator());
        sb.append("\"" + lista.get(0).getTag() + "\": {" + System.lineSeparator());
        lista.remove(0);
        sb.append("\"" + lista.get(0).getTag() + "\": [\n{" + System.lineSeparator());
        String tag = lista.get(0).getTag();
        lista.remove(0);
        Objeto anterior = new Objeto();
        for (Objeto obj : lista)
        {
            if (obj.getTag().equals(tag))
            {
                sb.append("\n},\n{\n");
                anterior = obj;
                continue;
            } else if (anterior.getValor() == null)
            {
                sb.append("\"" + obj.getTag() + "\": \"" + obj.getValor() + "\"");
                anterior = obj;
                continue;
            } else if (obj.getValor() != null)
            {
                sb.append(",\n");
                anterior = obj;
            }
            
            sb.append("\"" + obj.getTag() + "\": \"" + obj.getValor() + "\""); //Last Thing
        }
        sb.append("\n}\n]\n}\n}");
        File saida = new File("output.json");
        if (!saida.createNewFile())
        {
            saida.delete();
            saida.createNewFile();
        }
        try (FileWriter writer = new FileWriter("output.json"))
        {
            writer.write(sb.toString());
        }
        System.exit(0);
    }
    
}
