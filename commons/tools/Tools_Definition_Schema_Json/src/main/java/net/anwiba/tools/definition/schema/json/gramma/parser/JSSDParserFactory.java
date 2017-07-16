/*
 * #%L anwiba commons tools %% Copyright (C) 2007 - 2016 Andreas Bartels %% This program is free
 * software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 2.1 of the License,
 * or (at your option) any later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Lesser Public License for more details. You should
 * have received a copy of the GNU General Lesser Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/lgpl-2.1.html>. #L%
 */

package net.anwiba.tools.definition.schema.json.gramma.parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import net.anwiba.tools.definition.schema.json.gramma.JSSDLexer;
import net.anwiba.tools.definition.schema.json.gramma.JSSDParser;

public class JSSDParserFactory {

  public JSSDParser create(final InputStream inputStream, final String encoding, final ANTLRErrorListener errorListener)
      throws IOException {
    return create(new InputStreamReader(inputStream, encoding), errorListener);
  }

  public JSSDParser create(final Reader reader, final ANTLRErrorListener errorListener) throws IOException {
    return create(CharStreams.fromReader(reader), errorListener);
  }

  public JSSDParser create(final CharStream stream, final ANTLRErrorListener errorListener) {
    return create(new JSSDLexer(stream), errorListener);
  }

  public JSSDParser create(final JSSDLexer lexer, final ANTLRErrorListener errorListener) {
    if (errorListener != null) {
      lexer.removeErrorListeners();
    }
    lexer.addErrorListener(errorListener);
    final JSSDParser parser = new JSSDParser(new CommonTokenStream(lexer));
    if (errorListener != null) {
      parser.removeErrorListeners();
    }
    parser.addErrorListener(errorListener);
    return parser;
  }

}
