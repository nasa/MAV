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
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link BrahmsParser}.
 */
public interface BrahmsListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void enterCompilationUnit(BrahmsParser.CompilationUnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void exitCompilationUnit(BrahmsParser.CompilationUnitContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#packageDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterPackageDeclaration(BrahmsParser.PackageDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#packageDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitPackageDeclaration(BrahmsParser.PackageDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#importDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterImportDeclaration(BrahmsParser.ImportDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#importDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitImportDeclaration(BrahmsParser.ImportDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#typeDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterTypeDeclaration(BrahmsParser.TypeDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#typeDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitTypeDeclaration(BrahmsParser.TypeDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#groupDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterGroupDeclaration(BrahmsParser.GroupDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#groupDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitGroupDeclaration(BrahmsParser.GroupDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#groupName}.
	 * @param ctx the parse tree
	 */
	void enterGroupName(BrahmsParser.GroupNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#groupName}.
	 * @param ctx the parse tree
	 */
	void exitGroupName(BrahmsParser.GroupNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#agentDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterAgentDeclaration(BrahmsParser.AgentDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#agentDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitAgentDeclaration(BrahmsParser.AgentDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#agentName}.
	 * @param ctx the parse tree
	 */
	void enterAgentName(BrahmsParser.AgentNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#agentName}.
	 * @param ctx the parse tree
	 */
	void exitAgentName(BrahmsParser.AgentNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassDeclaration(BrahmsParser.ClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassDeclaration(BrahmsParser.ClassDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#className}.
	 * @param ctx the parse tree
	 */
	void enterClassName(BrahmsParser.ClassNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#className}.
	 * @param ctx the parse tree
	 */
	void exitClassName(BrahmsParser.ClassNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#objectDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterObjectDeclaration(BrahmsParser.ObjectDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#objectDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitObjectDeclaration(BrahmsParser.ObjectDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#objectName}.
	 * @param ctx the parse tree
	 */
	void enterObjectName(BrahmsParser.ObjectNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#objectName}.
	 * @param ctx the parse tree
	 */
	void exitObjectName(BrahmsParser.ObjectNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#conceptualMembership}.
	 * @param ctx the parse tree
	 */
	void enterConceptualMembership(BrahmsParser.ConceptualMembershipContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#conceptualMembership}.
	 * @param ctx the parse tree
	 */
	void exitConceptualMembership(BrahmsParser.ConceptualMembershipContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#groupMembership}.
	 * @param ctx the parse tree
	 */
	void enterGroupMembership(BrahmsParser.GroupMembershipContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#groupMembership}.
	 * @param ctx the parse tree
	 */
	void exitGroupMembership(BrahmsParser.GroupMembershipContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#groupBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterGroupBodyDeclaration(BrahmsParser.GroupBodyDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#groupBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitGroupBodyDeclaration(BrahmsParser.GroupBodyDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#classInheritance}.
	 * @param ctx the parse tree
	 */
	void enterClassInheritance(BrahmsParser.ClassInheritanceContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#classInheritance}.
	 * @param ctx the parse tree
	 */
	void exitClassInheritance(BrahmsParser.ClassInheritanceContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#activitiesList}.
	 * @param ctx the parse tree
	 */
	void enterActivitiesList(BrahmsParser.ActivitiesListContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#activitiesList}.
	 * @param ctx the parse tree
	 */
	void exitActivitiesList(BrahmsParser.ActivitiesListContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#activityDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterActivityDeclaration(BrahmsParser.ActivityDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#activityDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitActivityDeclaration(BrahmsParser.ActivityDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#primitiveActivity}.
	 * @param ctx the parse tree
	 */
	void enterPrimitiveActivity(BrahmsParser.PrimitiveActivityContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#primitiveActivity}.
	 * @param ctx the parse tree
	 */
	void exitPrimitiveActivity(BrahmsParser.PrimitiveActivityContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#communicateActivity}.
	 * @param ctx the parse tree
	 */
	void enterCommunicateActivity(BrahmsParser.CommunicateActivityContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#communicateActivity}.
	 * @param ctx the parse tree
	 */
	void exitCommunicateActivity(BrahmsParser.CommunicateActivityContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#broadcastActivity}.
	 * @param ctx the parse tree
	 */
	void enterBroadcastActivity(BrahmsParser.BroadcastActivityContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#broadcastActivity}.
	 * @param ctx the parse tree
	 */
	void exitBroadcastActivity(BrahmsParser.BroadcastActivityContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#javaActivity}.
	 * @param ctx the parse tree
	 */
	void enterJavaActivity(BrahmsParser.JavaActivityContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#javaActivity}.
	 * @param ctx the parse tree
	 */
	void exitJavaActivity(BrahmsParser.JavaActivityContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#lstOfConcepts}.
	 * @param ctx the parse tree
	 */
	void enterLstOfConcepts(BrahmsParser.LstOfConceptsContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#lstOfConcepts}.
	 * @param ctx the parse tree
	 */
	void exitLstOfConcepts(BrahmsParser.LstOfConceptsContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#transferList}.
	 * @param ctx the parse tree
	 */
	void enterTransferList(BrahmsParser.TransferListContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#transferList}.
	 * @param ctx the parse tree
	 */
	void exitTransferList(BrahmsParser.TransferListContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#whenDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterWhenDeclaration(BrahmsParser.WhenDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#whenDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitWhenDeclaration(BrahmsParser.WhenDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#timeOfWhen}.
	 * @param ctx the parse tree
	 */
	void enterTimeOfWhen(BrahmsParser.TimeOfWhenContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#timeOfWhen}.
	 * @param ctx the parse tree
	 */
	void exitTimeOfWhen(BrahmsParser.TimeOfWhenContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#transferDefinition}.
	 * @param ctx the parse tree
	 */
	void enterTransferDefinition(BrahmsParser.TransferDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#transferDefinition}.
	 * @param ctx the parse tree
	 */
	void exitTransferDefinition(BrahmsParser.TransferDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#transferType}.
	 * @param ctx the parse tree
	 */
	void enterTransferType(BrahmsParser.TransferTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#transferType}.
	 * @param ctx the parse tree
	 */
	void exitTransferType(BrahmsParser.TransferTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#transferAction}.
	 * @param ctx the parse tree
	 */
	void enterTransferAction(BrahmsParser.TransferActionContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#transferAction}.
	 * @param ctx the parse tree
	 */
	void exitTransferAction(BrahmsParser.TransferActionContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#activitySetup}.
	 * @param ctx the parse tree
	 */
	void enterActivitySetup(BrahmsParser.ActivitySetupContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#activitySetup}.
	 * @param ctx the parse tree
	 */
	void exitActivitySetup(BrahmsParser.ActivitySetupContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#paramDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterParamDeclaration(BrahmsParser.ParamDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#paramDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitParamDeclaration(BrahmsParser.ParamDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#param}.
	 * @param ctx the parse tree
	 */
	void enterParam(BrahmsParser.ParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#param}.
	 * @param ctx the parse tree
	 */
	void exitParam(BrahmsParser.ParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#priorityDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterPriorityDeclaration(BrahmsParser.PriorityDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#priorityDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitPriorityDeclaration(BrahmsParser.PriorityDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#maxDurationDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMaxDurationDeclaration(BrahmsParser.MaxDurationDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#maxDurationDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMaxDurationDeclaration(BrahmsParser.MaxDurationDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#minDurationDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMinDurationDeclaration(BrahmsParser.MinDurationDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#minDurationDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMinDurationDeclaration(BrahmsParser.MinDurationDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#randomDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterRandomDeclaration(BrahmsParser.RandomDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#randomDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitRandomDeclaration(BrahmsParser.RandomDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#priorityOrDurationValue}.
	 * @param ctx the parse tree
	 */
	void enterPriorityOrDurationValue(BrahmsParser.PriorityOrDurationValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#priorityOrDurationValue}.
	 * @param ctx the parse tree
	 */
	void exitPriorityOrDurationValue(BrahmsParser.PriorityOrDurationValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#randomValue}.
	 * @param ctx the parse tree
	 */
	void enterRandomValue(BrahmsParser.RandomValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#randomValue}.
	 * @param ctx the parse tree
	 */
	void exitRandomValue(BrahmsParser.RandomValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#workFramesList}.
	 * @param ctx the parse tree
	 */
	void enterWorkFramesList(BrahmsParser.WorkFramesListContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#workFramesList}.
	 * @param ctx the parse tree
	 */
	void exitWorkFramesList(BrahmsParser.WorkFramesListContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#thoughtFramesList}.
	 * @param ctx the parse tree
	 */
	void enterThoughtFramesList(BrahmsParser.ThoughtFramesListContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#thoughtFramesList}.
	 * @param ctx the parse tree
	 */
	void exitThoughtFramesList(BrahmsParser.ThoughtFramesListContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#workFrameDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterWorkFrameDeclaration(BrahmsParser.WorkFrameDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#workFrameDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitWorkFrameDeclaration(BrahmsParser.WorkFrameDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#thoughtFrameDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterThoughtFrameDeclaration(BrahmsParser.ThoughtFrameDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#thoughtFrameDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitThoughtFrameDeclaration(BrahmsParser.ThoughtFrameDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#frameTypeDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFrameTypeDeclaration(BrahmsParser.FrameTypeDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#frameTypeDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFrameTypeDeclaration(BrahmsParser.FrameTypeDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#frameType}.
	 * @param ctx the parse tree
	 */
	void enterFrameType(BrahmsParser.FrameTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#frameType}.
	 * @param ctx the parse tree
	 */
	void exitFrameType(BrahmsParser.FrameTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaration(BrahmsParser.VariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaration(BrahmsParser.VariableDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#detectableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterDetectableDeclaration(BrahmsParser.DetectableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#detectableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitDetectableDeclaration(BrahmsParser.DetectableDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#preconditionDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterPreconditionDeclaration(BrahmsParser.PreconditionDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#preconditionDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitPreconditionDeclaration(BrahmsParser.PreconditionDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#preconditionList}.
	 * @param ctx the parse tree
	 */
	void enterPreconditionList(BrahmsParser.PreconditionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#preconditionList}.
	 * @param ctx the parse tree
	 */
	void exitPreconditionList(BrahmsParser.PreconditionListContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#workFrameBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterWorkFrameBodyDeclaration(BrahmsParser.WorkFrameBodyDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#workFrameBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitWorkFrameBodyDeclaration(BrahmsParser.WorkFrameBodyDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#workFrameBodyElement}.
	 * @param ctx the parse tree
	 */
	void enterWorkFrameBodyElement(BrahmsParser.WorkFrameBodyElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#workFrameBodyElement}.
	 * @param ctx the parse tree
	 */
	void exitWorkFrameBodyElement(BrahmsParser.WorkFrameBodyElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#thoughtFrameBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterThoughtFrameBodyDeclaration(BrahmsParser.ThoughtFrameBodyDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#thoughtFrameBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitThoughtFrameBodyDeclaration(BrahmsParser.ThoughtFrameBodyDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#thoughtFrameBodyElement}.
	 * @param ctx the parse tree
	 */
	void enterThoughtFrameBodyElement(BrahmsParser.ThoughtFrameBodyElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#thoughtFrameBodyElement}.
	 * @param ctx the parse tree
	 */
	void exitThoughtFrameBodyElement(BrahmsParser.ThoughtFrameBodyElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#activityRef}.
	 * @param ctx the parse tree
	 */
	void enterActivityRef(BrahmsParser.ActivityRefContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#activityRef}.
	 * @param ctx the parse tree
	 */
	void exitActivityRef(BrahmsParser.ActivityRefContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#activityName}.
	 * @param ctx the parse tree
	 */
	void enterActivityName(BrahmsParser.ActivityNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#activityName}.
	 * @param ctx the parse tree
	 */
	void exitActivityName(BrahmsParser.ActivityNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#initialBeliefsList}.
	 * @param ctx the parse tree
	 */
	void enterInitialBeliefsList(BrahmsParser.InitialBeliefsListContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#initialBeliefsList}.
	 * @param ctx the parse tree
	 */
	void exitInitialBeliefsList(BrahmsParser.InitialBeliefsListContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#initialFactsList}.
	 * @param ctx the parse tree
	 */
	void enterInitialFactsList(BrahmsParser.InitialFactsListContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#initialFactsList}.
	 * @param ctx the parse tree
	 */
	void exitInitialFactsList(BrahmsParser.InitialFactsListContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#initialBeliefOrFactDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInitialBeliefOrFactDeclaration(BrahmsParser.InitialBeliefOrFactDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#initialBeliefOrFactDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInitialBeliefOrFactDeclaration(BrahmsParser.InitialBeliefOrFactDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#valueExpression}.
	 * @param ctx the parse tree
	 */
	void enterValueExpression(BrahmsParser.ValueExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#valueExpression}.
	 * @param ctx the parse tree
	 */
	void exitValueExpression(BrahmsParser.ValueExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#relationalExpression}.
	 * @param ctx the parse tree
	 */
	void enterRelationalExpression(BrahmsParser.RelationalExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#relationalExpression}.
	 * @param ctx the parse tree
	 */
	void exitRelationalExpression(BrahmsParser.RelationalExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(BrahmsParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(BrahmsParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#variableName}.
	 * @param ctx the parse tree
	 */
	void enterVariableName(BrahmsParser.VariableNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#variableName}.
	 * @param ctx the parse tree
	 */
	void exitVariableName(BrahmsParser.VariableNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#preCondition}.
	 * @param ctx the parse tree
	 */
	void enterPreCondition(BrahmsParser.PreConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#preCondition}.
	 * @param ctx the parse tree
	 */
	void exitPreCondition(BrahmsParser.PreConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#noValComparison}.
	 * @param ctx the parse tree
	 */
	void enterNoValComparison(BrahmsParser.NoValComparisonContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#noValComparison}.
	 * @param ctx the parse tree
	 */
	void exitNoValComparison(BrahmsParser.NoValComparisonContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#evalComparison}.
	 * @param ctx the parse tree
	 */
	void enterEvalComparison(BrahmsParser.EvalComparisonContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#evalComparison}.
	 * @param ctx the parse tree
	 */
	void exitEvalComparison(BrahmsParser.EvalComparisonContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#resultComparison}.
	 * @param ctx the parse tree
	 */
	void enterResultComparison(BrahmsParser.ResultComparisonContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#resultComparison}.
	 * @param ctx the parse tree
	 */
	void exitResultComparison(BrahmsParser.ResultComparisonContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#detectValComp}.
	 * @param ctx the parse tree
	 */
	void enterDetectValComp(BrahmsParser.DetectValCompContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#detectValComp}.
	 * @param ctx the parse tree
	 */
	void exitDetectValComp(BrahmsParser.DetectValCompContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#detectRelComp}.
	 * @param ctx the parse tree
	 */
	void enterDetectRelComp(BrahmsParser.DetectRelCompContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#detectRelComp}.
	 * @param ctx the parse tree
	 */
	void exitDetectRelComp(BrahmsParser.DetectRelCompContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#evalValComp}.
	 * @param ctx the parse tree
	 */
	void enterEvalValComp(BrahmsParser.EvalValCompContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#evalValComp}.
	 * @param ctx the parse tree
	 */
	void exitEvalValComp(BrahmsParser.EvalValCompContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#consequence}.
	 * @param ctx the parse tree
	 */
	void enterConsequence(BrahmsParser.ConsequenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#consequence}.
	 * @param ctx the parse tree
	 */
	void exitConsequence(BrahmsParser.ConsequenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#consequenceResult}.
	 * @param ctx the parse tree
	 */
	void enterConsequenceResult(BrahmsParser.ConsequenceResultContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#consequenceResult}.
	 * @param ctx the parse tree
	 */
	void exitConsequenceResult(BrahmsParser.ConsequenceResultContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#resultValComp}.
	 * @param ctx the parse tree
	 */
	void enterResultValComp(BrahmsParser.ResultValCompContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#resultValComp}.
	 * @param ctx the parse tree
	 */
	void exitResultValComp(BrahmsParser.ResultValCompContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#factCertainty}.
	 * @param ctx the parse tree
	 */
	void enterFactCertainty(BrahmsParser.FactCertaintyContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#factCertainty}.
	 * @param ctx the parse tree
	 */
	void exitFactCertainty(BrahmsParser.FactCertaintyContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#beliefCertainty}.
	 * @param ctx the parse tree
	 */
	void enterBeliefCertainty(BrahmsParser.BeliefCertaintyContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#beliefCertainty}.
	 * @param ctx the parse tree
	 */
	void exitBeliefCertainty(BrahmsParser.BeliefCertaintyContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#relComp}.
	 * @param ctx the parse tree
	 */
	void enterRelComp(BrahmsParser.RelCompContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#relComp}.
	 * @param ctx the parse tree
	 */
	void exitRelComp(BrahmsParser.RelCompContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(BrahmsParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(BrahmsParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(BrahmsParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(BrahmsParser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#factor}.
	 * @param ctx the parse tree
	 */
	void enterFactor(BrahmsParser.FactorContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#factor}.
	 * @param ctx the parse tree
	 */
	void exitFactor(BrahmsParser.FactorContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary(BrahmsParser.PrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary(BrahmsParser.PrimaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#element}.
	 * @param ctx the parse tree
	 */
	void enterElement(BrahmsParser.ElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#element}.
	 * @param ctx the parse tree
	 */
	void exitElement(BrahmsParser.ElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#equalityOperator}.
	 * @param ctx the parse tree
	 */
	void enterEqualityOperator(BrahmsParser.EqualityOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#equalityOperator}.
	 * @param ctx the parse tree
	 */
	void exitEqualityOperator(BrahmsParser.EqualityOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#evaluationOperator}.
	 * @param ctx the parse tree
	 */
	void enterEvaluationOperator(BrahmsParser.EvaluationOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#evaluationOperator}.
	 * @param ctx the parse tree
	 */
	void exitEvaluationOperator(BrahmsParser.EvaluationOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#collectionIndex}.
	 * @param ctx the parse tree
	 */
	void enterCollectionIndex(BrahmsParser.CollectionIndexContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#collectionIndex}.
	 * @param ctx the parse tree
	 */
	void exitCollectionIndex(BrahmsParser.CollectionIndexContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#objAttr}.
	 * @param ctx the parse tree
	 */
	void enterObjAttr(BrahmsParser.ObjAttrContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#objAttr}.
	 * @param ctx the parse tree
	 */
	void exitObjAttr(BrahmsParser.ObjAttrContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#tupleObjectRef}.
	 * @param ctx the parse tree
	 */
	void enterTupleObjectRef(BrahmsParser.TupleObjectRefContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#tupleObjectRef}.
	 * @param ctx the parse tree
	 */
	void exitTupleObjectRef(BrahmsParser.TupleObjectRefContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#sglObjectRef}.
	 * @param ctx the parse tree
	 */
	void enterSglObjectRef(BrahmsParser.SglObjectRefContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#sglObjectRef}.
	 * @param ctx the parse tree
	 */
	void exitSglObjectRef(BrahmsParser.SglObjectRefContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(BrahmsParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(BrahmsParser.ValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#conceptClassDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterConceptClassDeclaration(BrahmsParser.ConceptClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#conceptClassDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitConceptClassDeclaration(BrahmsParser.ConceptClassDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#conceptObjectDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterConceptObjectDeclaration(BrahmsParser.ConceptObjectDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#conceptObjectDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitConceptObjectDeclaration(BrahmsParser.ConceptObjectDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#areaDefDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterAreaDefDeclaration(BrahmsParser.AreaDefDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#areaDefDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitAreaDefDeclaration(BrahmsParser.AreaDefDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#areaDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterAreaDeclaration(BrahmsParser.AreaDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#areaDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitAreaDeclaration(BrahmsParser.AreaDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#pathDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterPathDeclaration(BrahmsParser.PathDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#pathDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitPathDeclaration(BrahmsParser.PathDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#displayDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterDisplayDeclaration(BrahmsParser.DisplayDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#displayDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitDisplayDeclaration(BrahmsParser.DisplayDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#costDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterCostDeclaration(BrahmsParser.CostDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#costDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitCostDeclaration(BrahmsParser.CostDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#timeUnitDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterTimeUnitDeclaration(BrahmsParser.TimeUnitDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#timeUnitDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitTimeUnitDeclaration(BrahmsParser.TimeUnitDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#locationDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterLocationDeclaration(BrahmsParser.LocationDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#locationDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitLocationDeclaration(BrahmsParser.LocationDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#areaName}.
	 * @param ctx the parse tree
	 */
	void enterAreaName(BrahmsParser.AreaNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#areaName}.
	 * @param ctx the parse tree
	 */
	void exitAreaName(BrahmsParser.AreaNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#iconDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterIconDeclaration(BrahmsParser.IconDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#iconDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitIconDeclaration(BrahmsParser.IconDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#attributesList}.
	 * @param ctx the parse tree
	 */
	void enterAttributesList(BrahmsParser.AttributesListContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#attributesList}.
	 * @param ctx the parse tree
	 */
	void exitAttributesList(BrahmsParser.AttributesListContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#attributeDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterAttributeDeclaration(BrahmsParser.AttributeDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#attributeDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitAttributeDeclaration(BrahmsParser.AttributeDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#visibilityType}.
	 * @param ctx the parse tree
	 */
	void enterVisibilityType(BrahmsParser.VisibilityTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#visibilityType}.
	 * @param ctx the parse tree
	 */
	void exitVisibilityType(BrahmsParser.VisibilityTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#attributeType}.
	 * @param ctx the parse tree
	 */
	void enterAttributeType(BrahmsParser.AttributeTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#attributeType}.
	 * @param ctx the parse tree
	 */
	void exitAttributeType(BrahmsParser.AttributeTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#javaTypeDef}.
	 * @param ctx the parse tree
	 */
	void enterJavaTypeDef(BrahmsParser.JavaTypeDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#javaTypeDef}.
	 * @param ctx the parse tree
	 */
	void exitJavaTypeDef(BrahmsParser.JavaTypeDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#typeDef}.
	 * @param ctx the parse tree
	 */
	void enterTypeDef(BrahmsParser.TypeDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#typeDef}.
	 * @param ctx the parse tree
	 */
	void exitTypeDef(BrahmsParser.TypeDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#attributeName}.
	 * @param ctx the parse tree
	 */
	void enterAttributeName(BrahmsParser.AttributeNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#attributeName}.
	 * @param ctx the parse tree
	 */
	void exitAttributeName(BrahmsParser.AttributeNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#qualifiedName}.
	 * @param ctx the parse tree
	 */
	void enterQualifiedName(BrahmsParser.QualifiedNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#qualifiedName}.
	 * @param ctx the parse tree
	 */
	void exitQualifiedName(BrahmsParser.QualifiedNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#relationsList}.
	 * @param ctx the parse tree
	 */
	void enterRelationsList(BrahmsParser.RelationsListContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#relationsList}.
	 * @param ctx the parse tree
	 */
	void exitRelationsList(BrahmsParser.RelationsListContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#relationDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterRelationDeclaration(BrahmsParser.RelationDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#relationDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitRelationDeclaration(BrahmsParser.RelationDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#relationTypeDef}.
	 * @param ctx the parse tree
	 */
	void enterRelationTypeDef(BrahmsParser.RelationTypeDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#relationTypeDef}.
	 * @param ctx the parse tree
	 */
	void exitRelationTypeDef(BrahmsParser.RelationTypeDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#relationName}.
	 * @param ctx the parse tree
	 */
	void enterRelationName(BrahmsParser.RelationNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#relationName}.
	 * @param ctx the parse tree
	 */
	void exitRelationName(BrahmsParser.RelationNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#isTruthVal}.
	 * @param ctx the parse tree
	 */
	void enterIsTruthVal(BrahmsParser.IsTruthValContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#isTruthVal}.
	 * @param ctx the parse tree
	 */
	void exitIsTruthVal(BrahmsParser.IsTruthValContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#primitiveTypes}.
	 * @param ctx the parse tree
	 */
	void enterPrimitiveTypes(BrahmsParser.PrimitiveTypesContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#primitiveTypes}.
	 * @param ctx the parse tree
	 */
	void exitPrimitiveTypes(BrahmsParser.PrimitiveTypesContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#collectionTypes}.
	 * @param ctx the parse tree
	 */
	void enterCollectionTypes(BrahmsParser.CollectionTypesContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#collectionTypes}.
	 * @param ctx the parse tree
	 */
	void exitCollectionTypes(BrahmsParser.CollectionTypesContext ctx);
	/**
	 * Enter a parse tree produced by {@link BrahmsParser#repeatDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterRepeatDeclaration(BrahmsParser.RepeatDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link BrahmsParser#repeatDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitRepeatDeclaration(BrahmsParser.RepeatDeclarationContext ctx);
}
