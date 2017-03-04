/**
Copyright © 2016, United States Government, as represented
by the Administrator of the National Aeronautics and Space
Administration. All rights reserved.
 
The MAV - Modeling, analysis and visualization of ATM concepts
platform is licensed under the Apache License, Version 2.0
(the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the
License at http://www.apache.org/licenses/LICENSE-2.0. 
 
Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
either express or implied. See the License for the specific
language governing permissions and limitations under the
License.
**/

// Generated from Brahms.g4 by ANTLR 4.5.3
package gov.nasa.arc.brahms.parse;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link BrahmsParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface BrahmsVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#compilationUnit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompilationUnit(BrahmsParser.CompilationUnitContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#packageDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPackageDeclaration(BrahmsParser.PackageDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#importDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportDeclaration(BrahmsParser.ImportDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#typeDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeDeclaration(BrahmsParser.TypeDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#groupDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupDeclaration(BrahmsParser.GroupDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#groupName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupName(BrahmsParser.GroupNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#agentDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAgentDeclaration(BrahmsParser.AgentDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#agentName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAgentName(BrahmsParser.AgentNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#classDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassDeclaration(BrahmsParser.ClassDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#className}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassName(BrahmsParser.ClassNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#objectDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjectDeclaration(BrahmsParser.ObjectDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#objectName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjectName(BrahmsParser.ObjectNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#conceptualMembership}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConceptualMembership(BrahmsParser.ConceptualMembershipContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#groupMembership}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupMembership(BrahmsParser.GroupMembershipContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#groupBodyDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupBodyDeclaration(BrahmsParser.GroupBodyDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#classInheritance}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassInheritance(BrahmsParser.ClassInheritanceContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#activitiesList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivitiesList(BrahmsParser.ActivitiesListContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#activityDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivityDeclaration(BrahmsParser.ActivityDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#primitiveActivity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimitiveActivity(BrahmsParser.PrimitiveActivityContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#communicateActivity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCommunicateActivity(BrahmsParser.CommunicateActivityContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#broadcastActivity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBroadcastActivity(BrahmsParser.BroadcastActivityContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#javaActivity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJavaActivity(BrahmsParser.JavaActivityContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#lstOfConcepts}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLstOfConcepts(BrahmsParser.LstOfConceptsContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#transferList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTransferList(BrahmsParser.TransferListContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#whenDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhenDeclaration(BrahmsParser.WhenDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#timeOfWhen}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTimeOfWhen(BrahmsParser.TimeOfWhenContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#transferDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTransferDefinition(BrahmsParser.TransferDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#transferType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTransferType(BrahmsParser.TransferTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#transferAction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTransferAction(BrahmsParser.TransferActionContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#activitySetup}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivitySetup(BrahmsParser.ActivitySetupContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#paramDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParamDeclaration(BrahmsParser.ParamDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(BrahmsParser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#priorityDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPriorityDeclaration(BrahmsParser.PriorityDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#maxDurationDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMaxDurationDeclaration(BrahmsParser.MaxDurationDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#minDurationDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMinDurationDeclaration(BrahmsParser.MinDurationDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#randomDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRandomDeclaration(BrahmsParser.RandomDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#priorityOrDurationValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPriorityOrDurationValue(BrahmsParser.PriorityOrDurationValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#randomValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRandomValue(BrahmsParser.RandomValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#workFramesList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWorkFramesList(BrahmsParser.WorkFramesListContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#thoughtFramesList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThoughtFramesList(BrahmsParser.ThoughtFramesListContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#workFrameDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWorkFrameDeclaration(BrahmsParser.WorkFrameDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#thoughtFrameDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThoughtFrameDeclaration(BrahmsParser.ThoughtFrameDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#frameTypeDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFrameTypeDeclaration(BrahmsParser.FrameTypeDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#frameType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFrameType(BrahmsParser.FrameTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#variableDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclaration(BrahmsParser.VariableDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#detectableDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDetectableDeclaration(BrahmsParser.DetectableDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#preconditionDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPreconditionDeclaration(BrahmsParser.PreconditionDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#preconditionList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPreconditionList(BrahmsParser.PreconditionListContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#workFrameBodyDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWorkFrameBodyDeclaration(BrahmsParser.WorkFrameBodyDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#workFrameBodyElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWorkFrameBodyElement(BrahmsParser.WorkFrameBodyElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#thoughtFrameBodyDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThoughtFrameBodyDeclaration(BrahmsParser.ThoughtFrameBodyDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#thoughtFrameBodyElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThoughtFrameBodyElement(BrahmsParser.ThoughtFrameBodyElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#activityRef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivityRef(BrahmsParser.ActivityRefContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#activityName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActivityName(BrahmsParser.ActivityNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#initialBeliefsList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInitialBeliefsList(BrahmsParser.InitialBeliefsListContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#initialFactsList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInitialFactsList(BrahmsParser.InitialFactsListContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#initialBeliefOrFactDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInitialBeliefOrFactDeclaration(BrahmsParser.InitialBeliefOrFactDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#valueExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValueExpression(BrahmsParser.ValueExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#relationalExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationalExpression(BrahmsParser.RelationalExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#variable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(BrahmsParser.VariableContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#variableName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableName(BrahmsParser.VariableNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#preCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPreCondition(BrahmsParser.PreConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#noValComparison}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNoValComparison(BrahmsParser.NoValComparisonContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#evalComparison}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEvalComparison(BrahmsParser.EvalComparisonContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#resultComparison}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResultComparison(BrahmsParser.ResultComparisonContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#detectValComp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDetectValComp(BrahmsParser.DetectValCompContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#detectRelComp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDetectRelComp(BrahmsParser.DetectRelCompContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#evalValComp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEvalValComp(BrahmsParser.EvalValCompContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#consequence}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConsequence(BrahmsParser.ConsequenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#consequenceResult}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConsequenceResult(BrahmsParser.ConsequenceResultContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#resultValComp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitResultValComp(BrahmsParser.ResultValCompContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#factCertainty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFactCertainty(BrahmsParser.FactCertaintyContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#beliefCertainty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBeliefCertainty(BrahmsParser.BeliefCertaintyContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#relComp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelComp(BrahmsParser.RelCompContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(BrahmsParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm(BrahmsParser.TermContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFactor(BrahmsParser.FactorContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimary(BrahmsParser.PrimaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#element}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElement(BrahmsParser.ElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#equalityOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqualityOperator(BrahmsParser.EqualityOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#evaluationOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEvaluationOperator(BrahmsParser.EvaluationOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#collectionIndex}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCollectionIndex(BrahmsParser.CollectionIndexContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#objAttr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjAttr(BrahmsParser.ObjAttrContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#tupleObjectRef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTupleObjectRef(BrahmsParser.TupleObjectRefContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#sglObjectRef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSglObjectRef(BrahmsParser.SglObjectRefContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue(BrahmsParser.ValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#conceptClassDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConceptClassDeclaration(BrahmsParser.ConceptClassDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#conceptObjectDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConceptObjectDeclaration(BrahmsParser.ConceptObjectDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#areaDefDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAreaDefDeclaration(BrahmsParser.AreaDefDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#areaDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAreaDeclaration(BrahmsParser.AreaDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#pathDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPathDeclaration(BrahmsParser.PathDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#displayDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDisplayDeclaration(BrahmsParser.DisplayDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#costDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCostDeclaration(BrahmsParser.CostDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#timeUnitDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTimeUnitDeclaration(BrahmsParser.TimeUnitDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#locationDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLocationDeclaration(BrahmsParser.LocationDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#areaName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAreaName(BrahmsParser.AreaNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#iconDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIconDeclaration(BrahmsParser.IconDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#attributesList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttributesList(BrahmsParser.AttributesListContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#attributeDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttributeDeclaration(BrahmsParser.AttributeDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#visibilityType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVisibilityType(BrahmsParser.VisibilityTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#attributeType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttributeType(BrahmsParser.AttributeTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#javaTypeDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJavaTypeDef(BrahmsParser.JavaTypeDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#typeDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeDef(BrahmsParser.TypeDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#attributeName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAttributeName(BrahmsParser.AttributeNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#qualifiedName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQualifiedName(BrahmsParser.QualifiedNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#relationsList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationsList(BrahmsParser.RelationsListContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#relationDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationDeclaration(BrahmsParser.RelationDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#relationTypeDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationTypeDef(BrahmsParser.RelationTypeDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#relationName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationName(BrahmsParser.RelationNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#isTruthVal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIsTruthVal(BrahmsParser.IsTruthValContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#primitiveTypes}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimitiveTypes(BrahmsParser.PrimitiveTypesContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#collectionTypes}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCollectionTypes(BrahmsParser.CollectionTypesContext ctx);
	/**
	 * Visit a parse tree produced by {@link BrahmsParser#repeatDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRepeatDeclaration(BrahmsParser.RepeatDeclarationContext ctx);
}
