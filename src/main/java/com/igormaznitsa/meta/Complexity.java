/*
 * Copyright 2016 Igor Maznitsa.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.igormaznitsa.meta;

import javax.annotation.Nonnull;

/**
 * Complexity constants. List was taken from <a href="https://en.wikipedia.org/wiki/Time_complexity">the wiki page</a>.
 *
 * @since 1.1.2
 */
public enum Complexity {

  /**
   * <a href="https://en.wikipedia.org/wiki/Time_complexity#Constant_time">Constant value.</a> Example: Determining if an integer (represented in binary) is even or odd.
   * <p>
   * <b>O(1)</b></p>
   *
   * @since 1.1.2
   */
  CONSTANT("O(1)"),
  /**
   * <a href="https://en.wikipedia.org/wiki/Ackermann_function#Inverse">Inverse Ackermann.</a> Example: Amortized time per operation using a disjoint set.
   * <p>
   * <b>O(α(n))</b></p>
   *
   * @since 1.1.2
   */
  INVERSE_ACKERMANN("O(a(n))"),
  /**
   * <a href="https://en.wikipedia.org/wiki/Iterated_logarithm">Iterated logarithmic.</a> Example:
   * <a href="https://en.wikipedia.org/wiki/Graph_coloring#Parallel_and_distributed_algorithms">Distributed coloring of cycles.</a>
   * <p>
   * <b>O(log* n)</b></p>
   *
   * @since 1.1.2
   */
  ITERATED_LOGARITHMIC("O(log* n)"),
  /**
   * <a href="https://en.wikipedia.org/wiki/Time_complexity#Logarithmic_time">Log-logarithmic.</a> Example: Amortized time per operation using a bounded priority queue.
   * <p>
   * <b>O(log log n)</b></p>
   *
   * @since 1.1.2
   */
  LOG_LOGARITHMIC("O(log log n)"),
  /**
   * <a href="https://en.wikipedia.org/wiki/Time_complexity#Logarithmic_time">Logarithmic.</a> Example: <a href="https://en.wikipedia.org/wiki/Binary_search">Binary search</a>.
   * <p>
   * <b>O(log n)</b></p>
   *
   * @since 1.1.2
   */
  LOGARITHMIC("O(log n)"),
  /**
   * <a href="https://en.wikipedia.org/wiki/Time_complexity#Logarithmic_time">Polylogarithmic.</a>
   * <p>
   * <b>poly(log n)</b></p>
   *
   * @since 1.1.2
   */
  POLYLOGARITHMIC("poly(log n)"),
  /**
   * Fractional power. Example: <a href="https://en.wikipedia.org/wiki/Kd-tree">Searching in a kd-tree</a>.
   * <p>
   * <b>O(n<sup>c</sup>) where 0 < c < 1 </b></p>
   *
   * @since 1.1.2
   */
  FRACTIONAL_POWER("O(n^c)"),
  /**
   * <a href="https://en.wikipedia.org/wiki/Time_complexity#Linear_time">Linear</a>. Example: Finding the smallest or largest item in an unsorted array.
   * <p>
   * <b>O(n)</b></p>
   *
   * @since 1.1.2
   */
  LINEAR("O(n)"),
  /**
   * n log star n. Example: <a href="https://en.wikipedia.org/wiki/Polygon_triangulation">Seidel's polygon triangulation algorithm</a>.
   * <p>
   * <b>O(n log* n)</b></p>
   *
   * @since 1.1.2
   */
  N_LOG_STAR_N("O(n log* n)"),
  /**
   * <a href="https://en.wikipedia.org/wiki/Time_complexity#Linearithmic_time">Lineqarithmic</a>. Example: Fastest possible comparison sort.
   * <p>
   * <b>O(n log n)</b></p>
   *
   * @since 1.1.2
   */
  LINEARITHMIC("O(n log n)"),
  /**
   * Quadratic. Example: Bubble sort; Insertion sort; Direct convolution.
   * <p>
   * <b>O(n<sup>2</sup>)</b></p>
   *
   * @since 1.1.2
   */
  QUADRATIC("O(n^2)"),
  /**
   * Cubic. Example: Naive multiplication of two n×n matrices. Calculating partial correlation.
   * <p>
   * <b>O(n<sup>3</sup>)</b></p>
   *
   * @since 1.1.2
   */
  CUBIC("O(n^3)"),
  /**
   * <a href="https://en.wikipedia.org/wiki/Time_complexity#Polynomial_time">Polynomial</a>. Example: <a href="https://en.wikipedia.org/wiki/Karmarkar's_algorithm">Karmarkar's
   * algorithm for linear programming</a>; AKS primality test.
   * <p>
   * <b>2<sup>O(log n)</sup> = poly(n)</b></p>
   *
   * @since 1.1.2
   */
  POLYNOMIAL("poly(n)"),
  /**
   * <a href="https://en.wikipedia.org/wiki/Time_complexity#Quasi-polynomial_time">Quasi-polinomial</a>. Example: Best-known O(log2 n)-approximation algorithm for the directed
   * Steiner tree problem.
   * <p>
   * <b>2<sup>poly(log n)</sup></b></p>
   *
   * @since 1.1.2
   */
  QUASI_POLYNOMIAL("2^poly(log n)"),
  /**
   * <a href="https://en.wikipedia.org/wiki/Time_complexity#Sub-exponential_time">Sub-exponential</a>. Example: Assuming complexity theoretic conjectures, BPP is contained in
   * SUBEXP.
   * <p>
   * <b>O(2<sup>n<sup>ε</sup></sup>) for all ε > 0</b></p>
   *
   * @since 1.1.2
   */
  SUB_EXPONENTIAL("O(2^n^e)"),
  /**
   * <a href="https://en.wikipedia.org/wiki/Time_complexity#Exponential_time">Exponential</a>. Example: Solving matrix chain multiplication via brute-force search.
   * <p>
   * <b>2<sup>O(n)</sup></b></p>
   *
   * @since 1.1.2
   */
  EXPONENTIAL("2^O(n)"),
  /**
   * Factorial. Example: Solving the traveling salesman problem via brute-force search.
   * <p>
   * <b>O(n!)</b></p>
   *
   * @since 1.1.2
   */
  FACTORIAL("O(n!)"),
  /**
   * <a href="https://en.wikipedia.org/wiki/Time_complexity#Double_exponential_time">Double exponential</a>. Example: Deciding the truth of a given statement in Presburger
   * arithmetic.
   * <p>
   * <b>2<sup>2<sup>poly(n)</sup></sup></b></p>
   *
   * @since 1.1.2
   */
  DOUBLE_EXPONENTIAL("2^2^poly(n)");

  private final String formula;

  private Complexity(@Nonnull final String formula) {
    this.formula = formula;
  }

  /**
   * Get the formula.
   *
   * @return formula as string
   * @since 1.1.2
   */
  @Nonnull
  public String getFormula() {
    return this.formula;
  }

  @Override
  @Nonnull
  public String toString() {
    return this.formula;
  }
}
