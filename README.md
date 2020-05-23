# ArrayToHTMLTable
My bullshit project, solve a problem in [CodeWars](https://www.codewars.com/kata/5e7e4b7cd889f7001728fd4a/python) using Java with GUI and Database for store input and output
<h2 class="header">Overview</h2>
<p>The task is simple - given a 2D array (list), generate an HTML table representing the data from this array.</p>
<p>You need to write a function called <code>to_table</code>/<code>toTable</code>, that takes three arguments:</p>
<ul>
  <li><code>data</code> - a 2D array (list),</li>
  <li><code>headers</code> - an optional boolean value. If <code>True</code>, the first row of the array is considered a header (see below). Defaults to <code>False</code>,</li>
  <li><code>index</code> - an optional boolean value. If <code>True</code>, the first column in the table should contain 1-based indices of the corresponding row. If <code>headers</code> arguments is <code>True</code>, this column should have an empty header. Defaults to <code>False</code>.</li>
</ul>
and returns a string containing HTML tags representing the table.
<h2 class="header">Details</h2>
HTML table is structured like this:  

<pre><code class="language-xml"><span class="cm-tag cm-bracket">&lt;</span><span class="cm-tag">table</span><span class="cm-tag cm-bracket">&gt;</span>
  <span class="cm-tag cm-bracket">&lt;</span><span class="cm-tag">thead</span><span class="cm-tag cm-bracket">&gt;</span>                 <span class="cm-comment">&lt;!-- an optional table header --&gt;</span>
    <span class="cm-tag cm-bracket">&lt;</span><span class="cm-tag">tr</span><span class="cm-tag cm-bracket">&gt;</span>                  <span class="cm-comment">&lt;!-- a header row --&gt;</span>
      <span class="cm-tag cm-bracket">&lt;</span><span class="cm-tag">th</span><span class="cm-tag cm-bracket">&gt;</span>header1<span class="cm-tag cm-bracket">&lt;/</span><span class="cm-tag">th</span><span class="cm-tag cm-bracket">&gt;</span>    <span class="cm-comment">&lt;!-- a single header cell --&gt;</span>
      <span class="cm-tag cm-bracket">&lt;</span><span class="cm-tag">th</span><span class="cm-tag cm-bracket">&gt;</span>header2<span class="cm-tag cm-bracket">&lt;/</span><span class="cm-tag">th</span><span class="cm-tag cm-bracket">&gt;</span>
    <span class="cm-tag cm-bracket">&lt;/</span><span class="cm-tag">tr</span><span class="cm-tag cm-bracket">&gt;</span>
  <span class="cm-tag cm-bracket">&lt;/</span><span class="cm-tag">thead</span><span class="cm-tag cm-bracket">&gt;</span>
  <span class="cm-tag cm-bracket">&lt;</span><span class="cm-tag">tbody</span><span class="cm-tag cm-bracket">&gt;</span>                 <span class="cm-comment">&lt;!-- a table's body --&gt;</span>
    <span class="cm-tag cm-bracket">&lt;</span><span class="cm-tag">tr</span><span class="cm-tag cm-bracket">&gt;</span>                  <span class="cm-comment">&lt;!-- a table's row --&gt;</span>
      <span class="cm-tag cm-bracket">&lt;</span><span class="cm-tag">td</span><span class="cm-tag cm-bracket">&gt;</span>row1, col1<span class="cm-tag cm-bracket">&lt;/</span><span class="cm-tag">td</span><span class="cm-tag cm-bracket">&gt;</span> <span class="cm-comment">&lt;!-- a row's cell --&gt;</span>
      <span class="cm-tag cm-bracket">&lt;</span><span class="cm-tag">td</span><span class="cm-tag cm-bracket">&gt;</span>row1, col2<span class="cm-tag cm-bracket">&lt;/</span><span class="cm-tag">td</span><span class="cm-tag cm-bracket">&gt;</span>
    <span class="cm-tag cm-bracket">&lt;/</span><span class="cm-tag">tr</span><span class="cm-tag cm-bracket">&gt;</span>
    <span class="cm-tag cm-bracket">&lt;</span><span class="cm-tag">tr</span><span class="cm-tag cm-bracket">&gt;</span>
      <span class="cm-tag cm-bracket">&lt;</span><span class="cm-tag">td</span><span class="cm-tag cm-bracket">&gt;</span>row2, col1<span class="cm-tag cm-bracket">&lt;/</span><span class="cm-tag">td</span><span class="cm-tag cm-bracket">&gt;</span>
      <span class="cm-tag cm-bracket">&lt;</span><span class="cm-tag">td</span><span class="cm-tag cm-bracket">&gt;</span>row2, col2<span class="cm-tag cm-bracket">&lt;/</span><span class="cm-tag">td</span><span class="cm-tag cm-bracket">&gt;</span>
    <span class="cm-tag cm-bracket">&lt;/</span><span class="cm-tag">tr</span><span class="cm-tag cm-bracket">&gt;</span>
  <span class="cm-tag cm-bracket">&lt;/</span><span class="cm-tag">tbody</span><span class="cm-tag cm-bracket">&gt;</span>
<span class="cm-tag cm-bracket">&lt;/</span><span class="cm-tag">table</span><span class="cm-tag cm-bracket">&gt;</span></code></pre>
<p>The table header is optional. If <code>header</code> argument is <code>False</code> then the table starts with <code>&lt;tbody&gt;</code> tag, ommiting <code>&lt;thead&gt;</code>.</p>
<p>So, for example:</p>
<pre><code class="language-python"><span class="cm-variable">to_table</span>([[<span class="cm-string">"lorem"</span>, <span class="cm-string">"ipsum"</span>], [<span class="cm-string">"dolor"</span>, <span class="cm-string">"sit amet"</span>]], <span class="cm-keyword">True</span>, <span class="cm-keyword">True</span>)</code></pre>
<pre style="display: none;"><code class="language-javascript"><span class="cm-variable">toTable</span>([[<span class="cm-string">"lorem"</span>, <span class="cm-string">"ipsum"</span>], [<span class="cm-string">"dolor"</span>, <span class="cm-string">"sit amet"</span>]], <span class="cm-atom">true</span>, <span class="cm-atom">true</span>)</code></pre>
<p>returns</p>
<pre><code class="language-python"><span class="cm-string">"&lt;table&gt;&lt;thead&gt;&lt;tr&gt;&lt;th&gt;&lt;/th&gt;&lt;th&gt;lorem&lt;/th&gt;&lt;th&gt;ipsum&lt;/th&gt;&lt;/tr&gt;&lt;/thead&gt;&lt;tbody&gt;&lt;tr&gt;&lt;td&gt;1&lt;/td&gt;&lt;td&gt;dolor&lt;/td&gt;&lt;td&gt;sit amet&lt;/td&gt;&lt;/tr&gt;&lt;/tbody&gt;&lt;/table&gt;"</span></code></pre>
<p>As you can see, no linebreaks or whitespaces (except for the ones present in the array values) are included, so the HTML code is minified.</p>
<p><b><u>IMPORTANT NOTE:</u></b> if the value in the array happens to be <code>None</code>, the value of the according cell in the table should be en ampty string (<code>""</code>)! Otherwise, just use a string representation of the given value, so, dependent on the language, the output can be slightly different. No additional parsing is needed on the data.</p>


