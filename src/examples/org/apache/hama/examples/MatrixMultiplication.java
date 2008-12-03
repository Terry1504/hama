/**
 * Copyright 2007 The Apache Software Foundation
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hama.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hama.DenseMatrix;
import org.apache.hama.HamaConfiguration;
import org.apache.hama.Matrix;

public class MatrixMultiplication {
  private static HamaConfiguration conf = new HamaConfiguration();
  private static int row;
  private static int column;

  public static void main(String[] args) throws IOException {
    if (args.length < 2) {
      System.out.println("mult  [-m maps] [-r reduces] <row_m> <column_n>");
      System.exit(-1);
    } else {
      parseArgs(args);
    }

    DenseMatrix a = DenseMatrix.random_mapred(conf, row, column);
    DenseMatrix b = DenseMatrix.random_mapred(conf, row, column);
    
    if (!a.isBlocked())
      a.blocking_mapred(conf.getNumMapTasks());
    if (!b.isBlocked())
      b.blocking_mapred(conf.getNumMapTasks());

    Matrix c = a.mult(b);

    a.close();
    b.close();
    c.close();
  }

  private static void parseArgs(String[] args) {
    List<String> other_args = new ArrayList<String>();
    for (int i = 0; i < args.length; ++i) {
      try {
        if ("-m".equals(args[i])) {
          conf.setNumMapTasks(Integer.parseInt(args[++i]));
        } else if ("-r".equals(args[i])) {
          conf.setNumReduceTasks(Integer.parseInt(args[++i]));
        } else {
          other_args.add(args[i]);
        }
      } catch (NumberFormatException except) {
        System.out.println("ERROR: Integer expected instead of " + args[i]);
      } catch (ArrayIndexOutOfBoundsException except) {
        System.out.println("ERROR: Required parameter missing from "
            + args[i - 1]);
      }
    }

    row = Integer.parseInt(other_args.get(0));
    column = Integer.parseInt(other_args.get(1));
  }
}