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

package gov.nasa.arc.brahms.parse.model;

import java.util.HashSet;
import java.util.Set;

import gov.nasa.arc.brahms.model.Attribute;
import gov.nasa.arc.brahms.model.MultiAgentSystem;
import gov.nasa.arc.brahms.model.Relation;
import gov.nasa.arc.brahms.model.concept.Agent;
import gov.nasa.arc.brahms.model.concept.Basic;
import gov.nasa.arc.brahms.model.concept.Class_b;
import gov.nasa.arc.brahms.model.concept.Group;
import gov.nasa.arc.brahms.model.concept.Object_b;
import gov.nasa.arc.brahms.parse.BrahmsBaseListener;
import gov.nasa.arc.brahms.parse.BrahmsParser;

public class FirstPassBrahmsListener extends BrahmsBaseListener {
	
	public static String publicScope = "public";
	
	MultiAgentSystem mas;
	Set<Attribute> attributes;
	Set<Relation> relations;
	
	
	public FirstPassBrahmsListener(MultiAgentSystem mas) {
		this.mas = mas;
	}
	
	
	@Override 
	public void enterAgentDeclaration(BrahmsParser.AgentDeclarationContext ctx) {
		resetDataStructures();
	}
	
	
	@Override 
	public void enterClassDeclaration(BrahmsParser.ClassDeclarationContext ctx) {
		resetDataStructures();
		
	}
	
	@Override 
	public void enterGroupDeclaration(BrahmsParser.GroupDeclarationContext ctx) {
		resetDataStructures();
	}
	
	@Override 
	public void enterObjectDeclaration(BrahmsParser.ObjectDeclarationContext ctx) { 
		resetDataStructures();
	}
	
	@Override 
	public void exitClassDeclaration(BrahmsParser.ClassDeclarationContext ctx) {
		String className = ctx.className().getText();
		String display = getDisplay(className, ctx.groupBodyDeclaration());
		double cost = getCost(ctx.groupBodyDeclaration());
		int timeUnit = getTimeUnit(ctx.groupBodyDeclaration());
		Class_b classInstance = new Class_b(className, display, cost, timeUnit);

		addDataStructures(classInstance);
		mas.addClasses(className, classInstance);
		
		System.out.println("<<<< exit class declaration");
	}
	
	@Override 
	public void exitGroupDeclaration(BrahmsParser.GroupDeclarationContext ctx) {
		String grpName = ctx.groupName().getText();
		String display = getDisplay(grpName, ctx.groupBodyDeclaration());
		double cost = getCost(ctx.groupBodyDeclaration());
		int timeUnit = getTimeUnit(ctx.groupBodyDeclaration());
		
		Group group = new Group(grpName, display, cost, timeUnit);
		addDataStructures(group);
		
		mas.addGroups(group);
		System.out.println("<<<< exit group declaration");
	}

	@Override 
	public void exitAgentDeclaration(BrahmsParser.AgentDeclarationContext ctx) {
		String agentName = ctx.agentName().getText();
		String display = getDisplay(agentName, ctx.groupBodyDeclaration());
		double cost = getCost(ctx.groupBodyDeclaration());
		int timeUnit = getTimeUnit(ctx.groupBodyDeclaration());
		String location = getLocation(ctx.groupBodyDeclaration());
		Agent agent = new Agent(agentName, display, cost, timeUnit, location);
		
		addDataStructures(agent);
		mas.addAgents(agent);
		
		System.out.println("<<<< exit agent declaration");
	}
	
	@Override 
	public void enterValueExpression(BrahmsParser.ValueExpressionContext ctx) {
		System.out.println("<<<< enter a value expression");
	}
	
	@Override 
	public void exitValueExpression(BrahmsParser.ValueExpressionContext ctx) {
		if(ctx.value() != null) System.out.println("it has a value");
		else System.out.println("it has a sglObjRef");
		
		System.out.println(ctx.equalityOperator().getText());
		System.out.println("<<<<< exit a value expression");
	}
	
	@Override public void enterRelationalExpression(BrahmsParser.RelationalExpressionContext ctx) {
		System.out.println("<<<< enter a relational expression");
	}
	
	@Override public void exitRelationalExpression(BrahmsParser.RelationalExpressionContext ctx) {
		System.out.println("<<<< exit a relational expression");
	}


	@Override 
	public void exitObjectDeclaration(BrahmsParser.ObjectDeclarationContext ctx) { 
		String objectName = ctx.objectName().getText();
		String display = getDisplay(objectName, ctx.groupBodyDeclaration());
		double cost = getCost(ctx.groupBodyDeclaration());
		int timeUnit = getTimeUnit(ctx.groupBodyDeclaration());
		String location = getLocation(ctx.groupBodyDeclaration());

		Object_b brahmsObject = new Object_b(objectName, display, cost, timeUnit, location);
		addDataStructures(brahmsObject);
		mas.addObjects(brahmsObject);
		
		System.out.println("<<<< exit object declaration");
	}
	
	
	
	@Override 
	public void enterAttributeDeclaration(BrahmsParser.AttributeDeclarationContext ctx) {
		System.out.println("<<<<< enter attribute declaration");
	}
	
	@Override 
	public void enterRelationDeclaration(BrahmsParser.RelationDeclarationContext ctx) { 
		System.out.println("<<<< enter relation declaration");
	}
	
	@Override 
	public void exitRelationDeclaration(BrahmsParser.RelationDeclarationContext ctx) { 

		String scope = getScope(ctx.visibilityType());
		String typeDef = ctx.relationTypeDef().getText();
		String relationName = ctx.relationName().getText();
		
		relations.add(new Relation(relationName, scope, typeDef));
		
		System.out.println("<<<< exit relation declaration");
	}
	
	@Override 
	public void exitAttributeDeclaration(BrahmsParser.AttributeDeclarationContext ctx) { 
		
		String scope = getScope(ctx.visibilityType());
		String typeDef = ctx.attributeType().typeDef().getText();
		String attrName = ctx.attributeName().getText();
		
		attributes.add(new Attribute(attrName, scope, typeDef));
		
	}
	
	private void resetDataStructures() {
		attributes = new HashSet<Attribute>();
		relations = new HashSet<Relation>();
	}
	
	private void addDataStructures(Basic b) {
		b.getAttributes().addAll(attributes);
		b.getRelations().addAll(relations);
	
	}
	
	private String getScope(BrahmsParser.VisibilityTypeContext ctx) {
		String scope = publicScope;
		if(ctx != null)
			scope = ctx.getText();
		return scope;
	}
	
	private String getDisplay(String name, BrahmsParser.GroupBodyDeclarationContext ctx) {
		if(ctx.displayDeclaration() == null) return name;
		return ctx.displayDeclaration().StringLiteral().getText();
	}
	
	private double getCost(BrahmsParser.GroupBodyDeclarationContext ctx) {
		if(ctx.costDeclaration() == null) return 0.0;
		return Double.valueOf(ctx.costDeclaration().Number().getText());
	}
	
	private int getTimeUnit(BrahmsParser.GroupBodyDeclarationContext ctx) {
		if(ctx.timeUnitDeclaration() == null) return 0;
		return Integer.valueOf(ctx.timeUnitDeclaration().Number().getText());
	}
	
	private String getLocation(BrahmsParser.GroupBodyDeclarationContext ctx) {
		if(ctx.locationDeclaration() == null) return "none";
		return ctx.locationDeclaration().areaName().Identifier().getText();
	}
	
}
