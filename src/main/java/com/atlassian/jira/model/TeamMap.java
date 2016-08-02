package com.atlassian.jira.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.ao.RelationService;
import com.atlassian.jira.ao.RelationshipService;
import com.atlassian.jira.ao.SavedRelationship;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.jira.webwork.WebAction;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;

public class TeamMap {
	private SearchRequest filter;
	private Relation relation;
	private List<Issue> issues;
	private Map<Relation,Integer> relationOccurence;
	private Map<ApplicationUser,Integer> userInvolvation;
	private Map<ApplicationUser,Map<ApplicationUser,MapCell>> teamMap;
	private List<ApplicationUser> userList;
	private List<Relation> relations;
	private List<Relationship> relationships;
	private List<Relationship> totalRelationships;
	private SearchService searchService;
	private int issuesCount;
	private int totalRelationShipsCount;
	private int relationShipCount;
	private RelationService relationService;
	private RelationshipService relationshipService;

	public TeamMap(SearchRequest filter, SearchService searchService,
			RelationService relationService, RelationshipService relationshipService) {
		this.setFilter(filter);
		this.searchService = searchService;
		this.relationService=relationService;
		this.relationshipService=relationshipService;
	}
	
	public TeamMap(Query query, SearchService searchService, RelationService relationService,
			RelationshipService relationshipService) {
		SearchRequest tempFilter=new SearchRequest();
		
		WebAction action = new WebAction();
		ApplicationUser user = action.getLoggedInUser();
		tempFilter.setOwner(user);
		tempFilter.setQuery(query);
		
		this.setFilter(tempFilter);
		this.searchService = searchService;
		this.relationService=relationService;
		this.relationshipService=relationshipService;
	}

	public void generateEmptyMap(){
		teamMap=new HashMap<ApplicationUser,Map<ApplicationUser,MapCell>>();
		Map<ApplicationUser,MapCell> rowMap;
		for(ApplicationUser userRow : userList){
			rowMap=new HashMap<ApplicationUser,MapCell>();
			for(ApplicationUser userCol : userList){
				rowMap.put(userCol, new MapCell());
			}
			teamMap.put(userRow, rowMap);
		}
	}
	
	public void fillMap(){
		for(Relationship relationship : relationships){
			Map<ApplicationUser,MapCell> rowMap=teamMap.get(relationship.getFirstUser());
			MapCell cell=rowMap.get(relationship.getSecondUser());
			cell.setValue(cell.getValue() + 1);
			cell.setTencity((double)cell.getValue()/(double)relationships.size());
			rowMap.put(relationship.getSecondUser(), cell);
			teamMap.put(relationship.getFirstUser(), rowMap);
		}
	}

	public void initiate() throws SearchException, ItemException {
		this.setIssues(getIssuesFromFilter(this.filter));
		this.setIssuesCount(issues.size());
		this.totalRelationships=getTotalRelationships();
		this.setTotalRelationShipsCount(totalRelationships.size());
		this.initRelationList();
	}

	private List<Issue> getIssuesFromFilter(SearchRequest filter) throws SearchException {
		Query query = filter.getQuery();
		WebAction action = new WebAction();
		ApplicationUser user = action.getLoggedInUser();
		PagerFilter pagerFilter = PagerFilter.getUnlimitedFilter();
		com.atlassian.jira.issue.search.SearchResults searchResults = null;
		searchResults = searchService.search(user, query, pagerFilter);
		return searchResults.getIssues();
	}
	
	private void initRelationList() {
		Map<Relation,Integer> relaationOccurence=new HashMap<Relation,Integer>();
		List<Relation> relationsList=new ArrayList<Relation>();
		for(Relationship rel : totalRelationships) {
			Relation relation=rel.getRelation();
			if(!relationsList.contains(relation)){
				relationsList.add(relation);
				relaationOccurence.put(relation, 1);
			}else{
				int occurence=relaationOccurence.get(relation);
				occurence++;
				relaationOccurence.put(relation,occurence);
			}
		}
		this.setRelationOccurence(relaationOccurence);
		this.setRelations(relationsList);
	}
	
	public List<Relationship> getTotalRelationships() {
		List<Relationship> totalRelationships=new ArrayList<Relationship>();
		for(Issue issue : issues){
			List<Relationship> issueRelationships=new ArrayList<Relationship>();
			for(SavedRelationship savedRelationship : relationshipService.allforIssue(issue.getId())) {
				Relationship relationship;
					try {
						relationship = new Relationship(savedRelationship,relationService);
						issueRelationships.add(relationship);
					} catch (ItemException e) { }
					
			}
			totalRelationships.addAll(issueRelationships);
			
		}
		return totalRelationships;
	}
	
	public List<ColorBar> getFilterRelationBar(){
		List<ColorBar> colorbars=new ArrayList<ColorBar>();
		ColorBar defultColorBar=new ColorBar(5,100,"No Relationship");
		if(relations==null){
			colorbars.add(defultColorBar);
			return colorbars;
		}
		if(relations.size()==0){
			colorbars.add(defultColorBar);
			return colorbars;
		}
		for(Relation rel : relations){
			int count=relationOccurence.get(rel);
			double percentage=((double)count/(double)totalRelationShipsCount)*99.99;
			ColorBar newColorBar=new ColorBar(rel.getColor(),percentage,count+" "+rel.getTitle()+" Relations");
			colorbars.add(newColorBar);
		}
		
		return colorbars;
	}
	
	public List<ColorBar> getRelationUserBar() {
		List<ColorBar> colorbars=new ArrayList<ColorBar>();
		ColorBar defultColorBar=new ColorBar(5,100,"No User Involved");
		int color=0;
		if(relations==null){
			colorbars.add(defultColorBar);
			return colorbars;
		}
		if(relations.size()==0){
			colorbars.add(defultColorBar);
			return colorbars;
		}
		for(ApplicationUser user : userList){
			int count=userInvolvation.get(user);
			double percentage=((double)count/(double)(relationships.size()*2))*99.99;
			ColorBar newColorBar=new ColorBar(color,percentage,user.getDisplayName()+" Involved in "+count+" Relationships");
			colorbars.add(newColorBar);
			color++;
		}
		return colorbars;
	}
	
	public void generateRelationships() {
		userInvolvation=new HashMap<ApplicationUser,Integer>();
		userList=new ArrayList<ApplicationUser>();
		relationships=new ArrayList<Relationship>();
		for(Relationship relationship : totalRelationships ){
			if(relationship.getRelation().equals(relation)){
				relationships.add(relationship);
				if(!userList.contains(relationship.getFirstUser())){
					userList.add(relationship.getFirstUser());
					userInvolvation.put(relationship.getFirstUser(), 1);					
				}else{
					int occurence=userInvolvation.get(relationship.getFirstUser());
					occurence++;
					userInvolvation.put(relationship.getFirstUser(),occurence);
				}
				if(!userList.contains(relationship.getSecondUser())){
					userList.add(relationship.getSecondUser());
					userInvolvation.put(relationship.getSecondUser(), 1);					
				}else{
					int occurence=userInvolvation.get(relationship.getSecondUser());
					occurence++;
					userInvolvation.put(relationship.getSecondUser(),occurence);
				}
				
				
			}
		}
	}

	public SearchRequest getFilter() {
		return filter;
	}

	public void setFilter(SearchRequest filter) {
		this.filter = filter;
	}

	public Relation getRelation() {
		return relation;
	}

	public void setRelation(Relation relation) {
		this.relation = relation;
	}

	public List<Issue> getIssues() {
		return issues;
	}

	public void setIssues(List<Issue> issues) {
		this.issues = issues;
	}

	public int getTotalRelationShipsCount() {
		return totalRelationShipsCount;
	}

	public void setTotalRelationShipsCount(int totalRelationShipsCount) {
		this.totalRelationShipsCount = totalRelationShipsCount;
	}

	public int getIssuesCount() {
		return issuesCount;
	}

	public void setIssuesCount(int issuesCount) {
		this.issuesCount = issuesCount;
	}

	public int getRelationShipCount() {
		return relationShipCount;
	}

	public void setRelationShipCount(int relationShipCount) {
		this.relationShipCount = relationShipCount;
	}

	public List<Relationship> getRelationships() {
		return relationships;
	}

	public void setRelationships(List<Relationship> relationships) {
		this.relationships = relationships;
	}


	public Map<Relation,Integer> getRelationOccurence() {
		return relationOccurence;
	}

	public void setRelationOccurence(Map<Relation,Integer> relationOccurence) {
		this.relationOccurence = relationOccurence;
	}

	public List<Relation> getRelations() {
		return relations;
	}

	public void setRelations(List<Relation> relations) {
		this.relations = relations;
	}

	public Map<ApplicationUser,Map<ApplicationUser,MapCell>> getTeamMap() {
		return teamMap;
	}

	public void setTeamMap(Map<ApplicationUser,Map<ApplicationUser,MapCell>> teamMap) {
		this.teamMap = teamMap;
	}
	
	public List<ApplicationUser> getUserList() {
		return userList;
	}

	public void setUserList(List<ApplicationUser> userList) {
		this.userList = userList;
	}


}
