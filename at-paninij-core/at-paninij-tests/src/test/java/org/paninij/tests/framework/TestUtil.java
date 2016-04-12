/*******************************************************************************
 * This file is part of the Panini project at Iowa State University.
 *
 * @PaniniJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * @PaniniJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with @PaniniJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributors:
 *  Dr. Hridesh Rajan,
 *  Dalton Mills,
 *  David Johnston,
 *  Trey Erenberger
 *  Jackson Maddox
 *******************************************************************************/
package org.paninij.tests.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Properties;

import com.sun.tools.javac.util.Pair;

public class TestUtil {
    private static final String configFileName = "config.properties";
    
    private final File sourceFolder;
    private final LinkedList<Pair<String, Properties>> info;
    
    public TestUtil(File sourceFolder) {
        this.sourceFolder = sourceFolder;
        info = new LinkedList<>();
    }

    public void process() throws IOException {
        assert info.size() == 0 && sourceFolder.isDirectory();
        
        for (File f : sourceFolder.listFiles()) {
            if (f.isDirectory()) {
                processImpl("", f);
            }
        }
    }
    
    private void processImpl(String pkg, File file) throws IOException {
        if (!file.isDirectory())
            return;
        
        pkg += file.getName() + ".";
        for (File f : file.listFiles()) {
            if (f.isFile() && f.getName().equals(configFileName)) {
                Properties prop = read(f);
                info.add(new Pair<String, Properties>(pkg, prop));
            } else if (f.isDirectory()) {
                processImpl(pkg, f);
            }
        }
    }
    
    private Properties read(File f) throws IOException{
        if (!f.exists())
            return new Properties();
        
        Properties prop = new Properties();
        FileInputStream stream = new FileInputStream(f);
        prop.load(stream);
        return prop;
    }
    
    public ArrayList<ArrayList<String>> getUnits(String type) {
       ArrayList<ArrayList<String>> list = new ArrayList<>();
       
       for (Pair<String, Properties> pair : info) {
           if ("true".equals((String)pair.snd.get("_Group"))) {
               ArrayList<String> units = new ArrayList<>();
               for (Object o: pair.snd.keySet()) {
                   if (!"_Group".equals((String)o) && type.equals(pair.snd.getProperty((String)o))) {
                       units.add(pair.fst + (String)o);
                   }
               }
               list.add(units);
           } else {
               for (Object o: pair.snd.keySet()) {
                   ArrayList<String> units = new ArrayList<>();
                   if (type.equals(pair.snd.getProperty((String) o)))
                   units.add(pair.fst + (String)o);
                   list.add(units);
               }
           }
       }
       
       return list;
    }
}
