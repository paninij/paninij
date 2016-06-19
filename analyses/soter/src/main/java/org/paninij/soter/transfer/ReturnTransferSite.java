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
 * 	Dr. Hridesh Rajan,
 * 	Dalton Mills,
 * 	David Johnston,
 * 	Trey Erenberger
 *******************************************************************************/
package org.paninij.soter.transfer;

import static java.text.MessageFormat.format;

import javax.json.Json;
import javax.json.JsonObject;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAReturnInstruction;
import com.ibm.wala.util.intset.IntSet;

public class ReturnTransferSite extends TransferSite
{
    public ReturnTransferSite(CGNode node, IntSet transfers, SSAInstruction transferInstr)
    {
        super(node, transfers, transferInstr);
    }

    @Override
    public TransferSite.Kind getKind()
    {
        return Kind.RETURN;
    }
    
    @Override
    public JsonObject toJson()
    {
        SSAReturnInstruction retInstr = (SSAReturnInstruction) transferInstr;

        return Json.createObjectBuilder()
                   .add("kind", "INVOKE")
                   .add("transfers", transfers.toString())
                   .add("instruction", transferInstr.toString())
                   .add("iindex", retInstr.iindex)
                   .add("method", node.getMethod().getSignature())
                   .add("context", node.getContext().toString())
                   .build();
    }
    
    public String toString()
    {
        String fmt = "ReturnTransferSite(node = {0}, SSAInstruction = {1}, transfers = {2})";
        return format(fmt, node, transferInstr, transfers);
    }
 
}
