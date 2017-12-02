/**
 * Copyright (c) 2017, UCLA Software Engineering and Analysis Laboratory (SEAL)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */
package ut.seal.plugins.utils;

/**
 * @author Myoungkyu Song
 * @date Jan 3, 2014
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public interface ICriticsHTMLKeyword {
	final String	CSS_DRAG					= ".drag { position: relative; cursor: move;}";
	final String	CSS_P_L1					= "p.layerOne { color:#4C4C4C;font-weight:bold;font-family:Monaco;font-size: 12px }";

	final String	CSS_SPAN_LBASE				= "span.layerBase { color:#000000;font-weight:bold;font-family:Monaco;font-size: 12px }";
	final String	CSS_SPAN_LINSERT			= "span.layerInsert { color:#FF0040;font-weight:bold;font-family:Monaco;font-size: 12px }";
	final String	CSS_SPAN_LDELETE			= "span.layerDelete { color:#4B0082;font-weight:bold;font-family:Monaco;font-size: 12px }";
	final String	CSS_SPAN_LPARM				= "span.layerParm { color:#298A08;font-weight:bold;font-family:Monaco;font-size: 12px }";
	final String	CSS_SPAN_LSYNCOL			= "span.layerSynCol { color:#A901DB;font-weight:bold;font-family:Monaco;font-size: 12px }";
	final String	CSS_SPAN_LINSERTBG			= "span.layerInsertBg { background-color:#EFEFFB;font-weight:bold;font-family:Monaco;font-size: 12px }";
	final String	CSS_SPAN_LDELETEBG			= "span.layerDeleteBg { background-color:#FBEFEF;font-weight:bold;font-family:Monaco;font-size: 12px }";
	final String	CSS_SPAN_LMISSBG			= "span.layerMissBg { background-color:#F7FE2E;font-weight:bold;font-family:Monaco;font-size: 12px }";			// yellow
	final String	CSS_SPAN_LRECOMMENDBG		= "span.layerRecommendBg { background-color:#81F79F;font-weight:bold;font-family:Monaco;font-size: 12px }";	// green
	final String	CSS_SPAN_COMMENT			= "span.layerComment { color:#4C4C4C;font-weight:bold;font-style:italic;font-family:Monaco;font-size: 12px}";
	final String	CSS_SPAN_REGULAR			= "span.layerRegular { color:#4C4C4C;font-weight:bold;font-family:Monaco;font-size: 12px }";

	final String	HTML_ANGLE_L				= "&lt;";
	final String	HTML_ANGLE_R				= "&gt;";
	final String	HTML_BGN_BODY				= "<body>";
	final String	HTML_BGN_DRAG				= "<dvi class=\"drag\">";
	final String	HTML_BGN_HEAD				= "<head>";
	final String	HTML_BGN_P_L1				= "<p class=\"layerOne\">";

	final String	HTML_BGN_SPAN_LBASE			= "<span class=\"layerBase\">";
	final String	HTML_BGN_SPAN_LINSERT		= "<span class=\"layerInsert\">";
	final String	HTML_BGN_SPAN_LDELETE		= "<span class=\"layerDelete\">";
	final String	HTML_BGN_SPAN_LPARM			= "<span class=\"layerParm\">";
	final String	HTML_BGN_SPAN_LSYNCOL		= "<span class=\"layerSynCol\">";
	final String	HTML_BGN_SPAN_LINSERTBG		= "<span class=\"layerInsertBg\">";
	final String	HTML_BGN_SPAN_LDELETEBG		= "<span class=\"layerDeleteBg\">";
	final String	HTML_BGN_SPAN_LMISSBG		= "<span class=\"layerMissBg\">";
	final String	HTML_BGN_SPAN_LRECOMMENDBG	= "<span class=\"layerRecommendBg\">";

	final String	HTML_BGN_SPAN_COMMENT		= "<span class=\"layerComment\">";
	final String	HTML_BGN_SPAN_REGULAR		= "<span class=\"layerRegular\">";
	final String	HTML_BGN_STYLE				= "<style>";

	final String	HTML_BR						= "<br>";
	final String	HTML_END_BODY				= "</body>";
	final String	HTML_END_DRAG				= "</dvi>";
	final String	HTML_END_HEAD				= "</head>";
	final String	HTML_END_P					= "</p>";
	final String	HTML_END_STYLE				= "</style>";

	final String	HTML_HEADER1				= "<h1>%s</h1>";
	final String	HTML_HEADER2				= "<h2>%s</h2>";
	final String	HTML_HEADER3				= "<h3>%s</h3>";

	final String	HTML_Space					= "&nbsp;";
	final String	TAB							= HTML_Space + HTML_Space + HTML_Space + HTML_Space;

	final String	HTML_SPAN_END				= "</span>";
	final String	HTML_SPAN_PRFIX				= "<span class";

	final String	HTML_T1						= TAB + "%s" + HTML_BR;
	final String	HTML_T2						= TAB + TAB + "%s" + HTML_BR;
	final String	HTML_T3						= TAB + TAB + TAB + "%s" + HTML_BR;
	final String	HTML_T4						= TAB + TAB + TAB + TAB + "%s" + HTML_BR;
	final String	HTML_T5						= TAB + TAB + TAB + TAB + TAB + "%s" + HTML_BR;
}
